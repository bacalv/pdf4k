import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.sl.usermodel.TableCell.BorderEdge;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xslf.usermodel.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts an Excel sheet (.xlsx) into a PowerPoint (.pptx) table, preserving
 * cell fill colors, borders, font styling, alignment, and merged cells (both
 * within the data area and within a repeating multi-row header block).
 *
 * Two things beyond plain XSLFTable.mergeCells() are handled specifically
 * because real-world "header-looking" spreadsheets often don't use a true
 * Excel merge:
 *
 *   1. "Center Across Selection" (Home > Alignment > Horizontal > "Center
 *      Across Selection") is a separate Excel feature that *looks* merged
 *      but is NOT a merged range -- sheet.getMergedRegions() won't report
 *      it. If it's not detected, the anchor cell's long header text gets
 *      crammed into a single narrow column and wraps into many stacked
 *      lines. This class detects runs of adjacent cells sharing that
 *      alignment and treats them as synthetic merges.
 *   2. Column widths are sized from actual cell text length (not from
 *      Excel's own column width metadata), with a single global font-scale
 *      factor applied if the natural widths don't fit the slide -- shrinking
 *      font size a bit keeps numbers on one line instead of wrapping them
 *      into two.
 *
 * The table is not editable / not linked back to Excel -- it's a static
 * visual copy, rendered natively as PowerPoint shapes (not an image), so
 * text stays selectable and it renders crisply at any zoom.
 *
 * If the sheet has more rows than fit on one slide, it's split across
 * multiple slides. Pass headerRowCount > 0 to treat the sheet's first N rows
 * as a header block that's repeated at the top of every slide.
 *
 * Maven dependency (POI 5.5.1, current as of writing -- check Maven Central
 * for anything newer):
 *
 *   <dependency>
 *     <groupId>org.apache.poi</groupId>
 *     <artifactId>poi-ooxml</artifactId>
 *     <version>5.5.1</version>
 *   </dependency>
 *
 * Limitations (kept simple on purpose -- extend as needed):
 *   - .xlsx only (uses XSSFWorkbook). .xls would need HSSF-specific color
 *     palette lookups instead of XSSFColor.
 *   - A merged region that straddles the header/data boundary (starts inside
 *     the header block and ends inside the data area) is skipped.
 *   - Pagination is purely height-based. If a single merged/data cell in the
 *     data area is vertically merged across more rows than fit on one page,
 *     the merge is clipped at the page boundary -- its label only appears on
 *     the page where the merge begins, continuation pages show it blank.
 *     (Earlier drafts refused to ever split a merge across pages, which
 *     backfires badly for a report where a category label is merged down
 *     across dozens of sub-item rows: the whole rest of the sheet would get
 *     glued onto one oversized page instead of paginating at all.)
 *   - Theme-based colors (as opposed to explicit RGB) fall back to a default,
 *     since resolving a theme color to RGB requires reading the workbook's
 *     theme part separately.
 */
public class SpreadsheetToPptx {

    private static final double MARGIN_PT = 24;       // slide margin, in points
    private static final double TITLE_HEIGHT_PT = 28;  // space reserved for the page title
    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final Color DEFAULT_FILL = null;    // null = no fill (transparent)
    private static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    private static final double CHAR_WIDTH_FACTOR = 0.52; // avg character width as a fraction of font size
    private static final double CELL_PADDING_PT = 8;      // horizontal padding inside a cell (both sides combined)
    private static final double MIN_COLUMN_WIDTH_PT = 20;
    private static final double MIN_FONT_PT = 6.0;        // floor for the auto-shrink font scale
    private static final double DEFAULT_FONT_SIZE_PT = 11.0;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: SpreadsheetToPptx <input.xlsx> <output.pptx> [sheetIndex] [headerRowCount]");
            return;
        }
        String inputPath = args[0];
        String outputPath = args[1];
        int sheetIndex = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        int headerRowCount = args.length > 3 ? Integer.parseInt(args[3]) : 0;

        convert(inputPath, outputPath, sheetIndex, headerRowCount);
        System.out.println("Wrote " + outputPath);
    }

    /**
     * @param headerRowCount number of leading sheet rows to treat as a header
     *                       block that's repeated on every slide (0 = no repeated header)
     */
    public static void convert(String excelPath, String pptxPath, int sheetIndex, int headerRowCount) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             XMLSlideShow ppt = new XMLSlideShow()) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter();
            int sheetFirstRow = sheet.getFirstRowNum();
            if (sheetFirstRow < 0) sheetFirstRow = 0;

            List<RowSnapshot> rows = readRows(sheet, evaluator, formatter, sheetFirstRow);
            if (rows.isEmpty()) {
                try (FileOutputStream out = new FileOutputStream(pptxPath)) {
                    ppt.write(out);
                }
                return;
            }
            headerRowCount = Math.max(0, Math.min(headerRowCount, rows.size()));
            int numCols = Math.max(1, rows.stream().mapToInt(r -> r.cells.size()).max().orElse(0));

            // Real Excel merges + synthetic "Center Across Selection" runs, both row-indices
            // relative to `rows` (i.e. already offset by sheetFirstRow).
            List<int[]> realMerges = readMerges(sheet, sheetFirstRow);
            List<int[]> casMerges = detectCenterAcrossSelectionRuns(sheet, sheetFirstRow, rows.size(), numCols);
            List<int[]> merges = new ArrayList<>(realMerges);
            merges.addAll(casMerges);

            List<int[]> headerMerges = new ArrayList<>();
            List<int[]> dataMerges = new ArrayList<>();
            Set<Long> covered = new HashSet<>();      // non-anchor cells of any merge, keyed by (row, col)
            Set<Long> wideMergeAnchors = new HashSet<>(); // anchors of merges spanning >1 column
            for (int[] m : merges) {
                markCovered(covered, m);
                if (m[3] > m[2]) wideMergeAnchors.add(cellKey(m[0], m[2]));
                if (m[1] < headerRowCount) {
                    headerMerges.add(m);
                } else if (m[0] >= headerRowCount) {
                    dataMerges.add(m);
                }
                // merges straddling the header/data boundary are skipped (documented limitation)
            }

            Dimension pageSize = ppt.getPageSize(); // points
            double availableWidth = pageSize.getWidth() - 2 * MARGIN_PT;
            double availableTableHeight = pageSize.getHeight() - 2 * MARGIN_PT - TITLE_HEIGHT_PT;

            double fontScale = computeFontScale(rows, numCols, availableWidth, wideMergeAnchors, covered);
            double[] colWidths = computeColumnWidths(rows, numCols, availableWidth, fontScale, wideMergeAnchors, covered);

            double headerHeight = 0;
            for (int r = 0; r < headerRowCount; r++) headerHeight += rows.get(r).heightPt;

            List<List<Integer>> pages = paginate(rows, headerRowCount, availableTableHeight, headerHeight);

            String sheetName = sheet.getSheetName();
            for (int p = 0; p < pages.size(); p++) {
                List<Integer> dataRowIdx = pages.get(p);
                XSLFSlide slide = ppt.createSlide();
                addTitle(ppt, slide, sheetName + (pages.size() > 1 ? "  (page " + (p + 1) + " of " + pages.size() + ")" : ""));

                int tblRows = dataRowIdx.size() + headerRowCount;
                XSLFTable table = slide.createTable(tblRows, numCols);
                table.setAnchor(new Rectangle2D.Double(MARGIN_PT, MARGIN_PT + TITLE_HEIGHT_PT, availableWidth, availableTableHeight));

                for (int r = 0; r < headerRowCount; r++) {
                    writeRow(table, r, rows.get(r), r, numCols, covered, fontScale);
                }
                for (int j = 0; j < dataRowIdx.size(); j++) {
                    int sheetRow = dataRowIdx.get(j);
                    writeRow(table, headerRowCount + j, rows.get(sheetRow), sheetRow, numCols, covered, fontScale);
                }

                for (int c = 0; c < numCols; c++) {
                    table.setColumnWidth(c, colWidths[c]);
                }
                for (int r = 0; r < headerRowCount; r++) {
                    table.getRows().get(r).setHeight(Math.max(rows.get(r).heightPt, 10));
                }
                for (int j = 0; j < dataRowIdx.size(); j++) {
                    table.getRows().get(headerRowCount + j).setHeight(Math.max(rows.get(dataRowIdx.get(j)).heightPt, 10));
                }

                for (int[] m : headerMerges) {
                    table.mergeCells(m[0], m[1], m[2], m[3]);
                }
                if (!dataRowIdx.isEmpty()) {
                    int pageFirst = dataRowIdx.get(0);
                    int pageLast = dataRowIdx.get(dataRowIdx.size() - 1);
                    for (int[] m : dataMerges) {
                        int clipFirst = Math.max(m[0], pageFirst);
                        int clipLast = Math.min(m[1], pageLast);
                        if (clipFirst > clipLast) continue; // this merge doesn't touch this page at all
                        int tblFirst = headerRowCount + (clipFirst - pageFirst);
                        int tblLast = headerRowCount + (clipLast - pageFirst);
                        table.mergeCells(tblFirst, tblLast, m[2], m[3]);
                    }
                }
            }

            try (FileOutputStream out = new FileOutputStream(pptxPath)) {
                ppt.write(out);
            }

            System.err.printf(
                    "SpreadsheetToPptx: sheet=%s rows=%d cols=%d headerRows=%d merges=%d (real=%d, centerAcrossSelection=%d) fontScale=%.2f pages=%d%n",
                    sheetName, rows.size(), numCols, headerRowCount, merges.size(), realMerges.size(), casMerges.size(), fontScale, pages.size());
        }
    }

    // ---------- merges ----------

    private static List<int[]> readMerges(Sheet sheet, int sheetFirstRow) {
        List<int[]> merges = new ArrayList<>();
        for (CellRangeAddress ra : sheet.getMergedRegions()) {
            merges.add(new int[]{
                    ra.getFirstRow() - sheetFirstRow, ra.getLastRow() - sheetFirstRow,
                    ra.getFirstColumn(), ra.getLastColumn()
            });
        }
        return merges;
    }

    /** Finds runs of 2+ adjacent cells in the same row sharing HorizontalAlignment.CENTER_SELECTION. */
    private static List<int[]> detectCenterAcrossSelectionRuns(Sheet sheet, int sheetFirstRow, int rowCount, int numCols) {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < rowCount; r++) {
            Row row = sheet.getRow(r + sheetFirstRow);
            if (row == null) continue;
            int runStart = -1;
            for (int c = 0; c <= numCols; c++) {
                boolean isCenterAcross = false;
                if (c < numCols) {
                    Cell cell = row.getCell(c);
                    if (cell != null) {
                        isCenterAcross = cell.getCellStyle().getAlignment() == HorizontalAlignment.CENTER_SELECTION;
                    }
                }
                if (isCenterAcross) {
                    if (runStart == -1) runStart = c;
                } else {
                    if (runStart != -1 && c - 1 > runStart) {
                        result.add(new int[]{r, r, runStart, c - 1});
                    }
                    runStart = -1;
                }
            }
        }
        return result;
    }

    private static void markCovered(Set<Long> covered, int[] m) {
        for (int r = m[0]; r <= m[1]; r++) {
            for (int c = m[2]; c <= m[3]; c++) {
                if (r == m[0] && c == m[2]) continue; // anchor cell keeps its content/style
                covered.add(cellKey(r, c));
            }
        }
    }

    // Row indices fit in 20 bits (Excel's row limit is 1,048,576); columns fit comfortably in the rest.
    private static long cellKey(int row, int col) {
        return ((long) row << 20) | (col & 0xFFFFF);
    }

    // ---------- data extraction ----------

    private static List<RowSnapshot> readRows(Sheet sheet, FormulaEvaluator evaluator, DataFormatter formatter, int sheetFirstRow) {
        List<RowSnapshot> result = new ArrayList<>();
        int lastRow = sheet.getLastRowNum();
        int numCols = 0;
        for (int r = sheetFirstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row != null) numCols = Math.max(numCols, row.getLastCellNum());
        }
        for (int r = sheetFirstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            RowSnapshot snap = new RowSnapshot();
            snap.heightPt = row != null ? row.getHeightInPoints() : sheet.getDefaultRowHeightInPoints();
            for (int c = 0; c < numCols; c++) {
                Cell cell = row != null ? row.getCell(c) : null;
                snap.cells.add(readCell(cell, evaluator, formatter));
            }
            result.add(snap);
        }
        return result;
    }

    private static CellSnapshot readCell(Cell cell, FormulaEvaluator evaluator, DataFormatter formatter) {
        CellSnapshot snap = new CellSnapshot();
        if (cell == null) return snap;

        snap.text = formatter.formatCellValue(cell, evaluator);

        CellStyle style = cell.getCellStyle();
        if (!(style instanceof XSSFCellStyle)) return snap;
        XSSFCellStyle xStyle = (XSSFCellStyle) style;

        if (xStyle.getFillPattern() == FillPatternType.SOLID_FOREGROUND) {
            snap.fill = toAwtColor(xStyle.getFillForegroundColorColor());
        }

        snap.borderTop = xStyle.getBorderTop();
        snap.borderBottom = xStyle.getBorderBottom();
        snap.borderLeft = xStyle.getBorderLeft();
        snap.borderRight = xStyle.getBorderRight();
        snap.borderTopColor = toAwtColor(xStyle.getTopBorderXSSFColor());
        snap.borderBottomColor = toAwtColor(xStyle.getBottomBorderXSSFColor());
        snap.borderLeftColor = toAwtColor(xStyle.getLeftBorderXSSFColor());
        snap.borderRightColor = toAwtColor(xStyle.getRightBorderXSSFColor());

        XSSFFont font = xStyle.getFont();
        snap.bold = font.getBold();
        snap.italic = font.getItalic();
        snap.fontSize = font.getFontHeightInPoints();
        Color fontColor = toAwtColor(font.getXSSFColor());
        snap.fontColor = fontColor != null ? fontColor : DEFAULT_FONT_COLOR;

        snap.align = mapAlign(xStyle.getAlignment());
        return snap;
    }

    private static Color toAwtColor(XSSFColor color) {
        if (color == null) return null;
        byte[] rgb = color.getRGB(); // resolves indexed colors; theme colors may return null
        if (rgb == null) return null;
        return new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF);
    }

    private static TextAlign mapAlign(HorizontalAlignment alignment) {
        if (alignment == null) return TextAlign.LEFT;
        switch (alignment) {
            case CENTER:
            case CENTER_SELECTION: return TextAlign.CENTER;
            case RIGHT: return TextAlign.RIGHT;
            case JUSTIFY: return TextAlign.JUSTIFY;
            default: return TextAlign.LEFT;
        }
    }

    // ---------- layout ----------

    /** Width (in points) a column needs, at the given font scale, to fit its longest cell on one line. */
    private static double naturalColumnWidth(List<RowSnapshot> rows, int col, double fontScale, Set<Long> wideMergeAnchors, Set<Long> covered) {
        double maxNeeded = MIN_COLUMN_WIDTH_PT;
        for (int r = 0; r < rows.size(); r++) {
            RowSnapshot row = rows.get(r);
            if (col >= row.cells.size()) continue;
            long key = cellKey(r, col);
            if (covered.contains(key) || wideMergeAnchors.contains(key)) continue; // width comes from elsewhere
            CellSnapshot cs = row.cells.get(col);
            String text = cs.text;
            if (text == null || text.isEmpty()) continue;
            double baseFont = cs.fontSize > 0 ? cs.fontSize : DEFAULT_FONT_SIZE_PT;
            double charFactor = CHAR_WIDTH_FACTOR * (cs.bold ? 1.1 : 1.0);
            double width = text.length() * (baseFont * fontScale) * charFactor + CELL_PADDING_PT;
            maxNeeded = Math.max(maxNeeded, width);
        }
        return maxNeeded;
    }

    /**
     * A single scale factor (<= 1.0) applied to every font size in the table so the sum of
     * natural column widths fits availableWidth, without shrinking any font below MIN_FONT_PT.
     * If even the floor font doesn't fit, returns the floor-based scale anyway (the table will
     * end up wider than the slide rather than becoming illegibly small).
     */
    private static double computeFontScale(List<RowSnapshot> rows, int numCols, double availableWidth, Set<Long> wideMergeAnchors, Set<Long> covered) {
        double naturalSum = 0;
        double minFontInSheet = DEFAULT_FONT_SIZE_PT;
        for (int c = 0; c < numCols; c++) naturalSum += naturalColumnWidth(rows, c, 1.0, wideMergeAnchors, covered);
        for (RowSnapshot row : rows) {
            for (CellSnapshot cs : row.cells) {
                if (cs.text != null && !cs.text.isEmpty()) {
                    double fs = cs.fontSize > 0 ? cs.fontSize : DEFAULT_FONT_SIZE_PT;
                    minFontInSheet = Math.min(minFontInSheet, fs);
                }
            }
        }
        if (naturalSum <= availableWidth || naturalSum <= 0) return 1.0;
        double scale = availableWidth / naturalSum;
        double minScale = MIN_FONT_PT / minFontInSheet;
        return Math.max(scale, minScale);
    }

    private static double[] computeColumnWidths(List<RowSnapshot> rows, int numCols, double availableWidth, double fontScale, Set<Long> wideMergeAnchors, Set<Long> covered) {
        double[] widths = new double[numCols];
        double sum = 0;
        for (int c = 0; c < numCols; c++) {
            widths[c] = naturalColumnWidth(rows, c, fontScale, wideMergeAnchors, covered);
            sum += widths[c];
        }
        // Grow columns proportionally to fill any leftover slide width; never shrink further here
        // (shrinking, if needed, already happened via fontScale).
        double growScale = sum > 0 ? Math.max(availableWidth / sum, 1.0) : 1.0;
        for (int c = 0; c < numCols; c++) widths[c] *= growScale;
        return widths;
    }

    /** Groups data-row indices (rows >= headerRowCount) into pages so each page's total height fits availableTableHeight. */
    private static List<List<Integer>> paginate(List<RowSnapshot> rows, int headerRowCount, double availableTableHeight, double headerHeight) {
        List<List<Integer>> pages = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        double currentHeight = headerHeight;

        for (int i = headerRowCount; i < rows.size(); i++) {
            double h = rows.get(i).heightPt;
            if (!current.isEmpty() && currentHeight + h > availableTableHeight) {
                pages.add(current);
                current = new ArrayList<>();
                currentHeight = headerHeight;
            }
            current.add(i);
            currentHeight += h;
        }
        if (!current.isEmpty() || pages.isEmpty()) pages.add(current);
        return pages;
    }

    private static void addTitle(XMLSlideShow ppt, XSLFSlide slide, String text) {
        Dimension pageSize = ppt.getPageSize();
        XSLFTextBox title = slide.createTextBox();
        title.setAnchor(new Rectangle2D.Double(MARGIN_PT, MARGIN_PT * 0.5, pageSize.getWidth() - 2 * MARGIN_PT, TITLE_HEIGHT_PT));
        XSLFTextParagraph para = title.addNewTextParagraph();
        XSLFTextRun run = para.addNewTextRun();
        run.setText(text);
        run.setFontSize(18.0);
        run.setBold(true);
    }

    private static void writeRow(XSLFTable table, int tblRowIdx, RowSnapshot rowSnap, int sheetRowIdx, int numCols, Set<Long> covered, double fontScale) {
        XSLFTableRow tr = table.getRows().get(tblRowIdx);
        for (int c = 0; c < numCols; c++) {
            XSLFTableCell cell = tr.getCells().get(c);
            if (covered.contains(cellKey(sheetRowIdx, c))) continue; // will be hidden once merged; leave it blank
            CellSnapshot cs = c < rowSnap.cells.size() ? rowSnap.cells.get(c) : new CellSnapshot();
            styleCell(cell, cs, fontScale);
        }
    }

    private static void styleCell(XSLFTableCell cell, CellSnapshot cs, double fontScale) {
        if (cs.fill != null) {
            cell.setFillColor(cs.fill);
        }

        applyBorder(cell, BorderEdge.top, cs.borderTop, cs.borderTopColor);
        applyBorder(cell, BorderEdge.bottom, cs.borderBottom, cs.borderBottomColor);
        applyBorder(cell, BorderEdge.left, cs.borderLeft, cs.borderLeftColor);
        applyBorder(cell, BorderEdge.right, cs.borderRight, cs.borderRightColor);

        List<XSLFTextParagraph> existing = cell.getTextParagraphs();
        XSLFTextParagraph para = existing.isEmpty() ? cell.addNewTextParagraph() : existing.get(0);
        para.setTextAlign(cs.align);
        XSLFTextRun run = para.addNewTextRun();
        run.setText(cs.text == null ? "" : cs.text);
        double baseFont = cs.fontSize > 0 ? cs.fontSize : DEFAULT_FONT_SIZE_PT;
        run.setFontSize(Math.max(MIN_FONT_PT, baseFont * fontScale));
        run.setBold(cs.bold);
        run.setItalic(cs.italic);
        run.setFontColor(cs.fontColor != null ? cs.fontColor : DEFAULT_FONT_COLOR);
    }

    private static void applyBorder(XSLFTableCell cell, BorderEdge edge, BorderStyle style, Color color) {
        if (style == null || style == BorderStyle.NONE) return;
        double width = borderWidthPt(style);
        cell.setBorderWidth(edge, width);
        cell.setBorderColor(edge, color != null ? color : DEFAULT_BORDER_COLOR);
    }

    private static double borderWidthPt(BorderStyle style) {
        switch (style) {
            case THICK: return 2.5;
            case MEDIUM:
            case MEDIUM_DASHED:
            case MEDIUM_DASH_DOT:
            case MEDIUM_DASH_DOT_DOT: return 1.5;
            case DOUBLE: return 2.0;
            default: return 0.75; // thin / hair / dashed / dotted, etc.
        }
    }

    // ---------- snapshots (decouple styling data from the live POI Excel objects) ----------

    private static class RowSnapshot {
        double heightPt;
        List<CellSnapshot> cells = new ArrayList<>();
    }

    private static class CellSnapshot {
        String text = "";
        Color fill = DEFAULT_FILL;
        Color fontColor = DEFAULT_FONT_COLOR;
        boolean bold = false;
        boolean italic = false;
        double fontSize = DEFAULT_FONT_SIZE_PT;
        TextAlign align = TextAlign.LEFT;
        BorderStyle borderTop = BorderStyle.NONE;
        BorderStyle borderBottom = BorderStyle.NONE;
        BorderStyle borderLeft = BorderStyle.NONE;
        BorderStyle borderRight = BorderStyle.NONE;
        Color borderTopColor;
        Color borderBottomColor;
        Color borderLeftColor;
        Color borderRightColor;
    }
}