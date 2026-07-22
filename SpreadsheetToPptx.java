import org.apache.poi.ss.usermodel.*;
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
import java.util.List;

/**
 * Converts an Excel sheet (.xlsx) into a PowerPoint (.pptx) table, preserving
 * cell fill colors, borders, font styling, and alignment as closely as
 * XSLFTable's API allows. The table is not editable / not linked back to
 * Excel -- it's a static visual copy, rendered natively as PowerPoint shapes
 * (not an image), so text stays selectable and it renders crisply at any zoom.
 *
 * If the sheet has more rows than fit on one slide, it's split across
 * multiple slides, optionally repeating a header row on each one.
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
 *   - Merged cells are not merged in the output table (each cell renders
 *     independently). XSLFTable does support merging via mergeCells(), so
 *     this is a reasonable follow-up if you need it.
 *   - Theme-based colors (as opposed to explicit RGB) fall back to a default,
 *     since resolving a theme color to RGB requires reading the workbook's
 *     theme part separately.
 */
public class SpreadsheetToPptx {

    private static final double MARGIN_PT = 24;      // slide margin, in points
    private static final double TITLE_HEIGHT_PT = 28; // space reserved for the page title
    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final Color DEFAULT_FILL = null;   // null = no fill (transparent)
    private static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: SpreadsheetToPptx <input.xlsx> <output.pptx> [sheetIndex] [repeatHeaderRow]");
            return;
        }
        String inputPath = args[0];
        String outputPath = args[1];
        int sheetIndex = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        boolean repeatHeaderRow = args.length > 3 && Boolean.parseBoolean(args[3]);

        convert(inputPath, outputPath, sheetIndex, repeatHeaderRow);
        System.out.println("Wrote " + outputPath);
    }

    public static void convert(String excelPath, String pptxPath, int sheetIndex, boolean repeatHeaderRow) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             XMLSlideShow ppt = new XMLSlideShow()) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter();

            List<RowSnapshot> rows = readRows(sheet, evaluator, formatter);
            if (rows.isEmpty()) {
                try (FileOutputStream out = new FileOutputStream(pptxPath)) {
                    ppt.write(out);
                }
                return;
            }
            int numCols = Math.max(1, rows.stream().mapToInt(r -> r.cells.size()).max().orElse(0));

            Dimension pageSize = ppt.getPageSize(); // points
            double availableWidth = pageSize.getWidth() - 2 * MARGIN_PT;
            double availableTableHeight = pageSize.getHeight() - 2 * MARGIN_PT - TITLE_HEIGHT_PT;

            double[] colWidths = computeColumnWidths(sheet, numCols, availableWidth);

            int headerRowIndex = repeatHeaderRow ? 0 : -1;
            double headerHeight = repeatHeaderRow ? rows.get(0).heightPt : 0;

            List<List<Integer>> pages = paginate(rows, headerRowIndex, availableTableHeight, headerHeight);

            String sheetName = sheet.getSheetName();
            for (int p = 0; p < pages.size(); p++) {
                List<Integer> dataRowIdx = pages.get(p);
                XSLFSlide slide = ppt.createSlide();
                addTitle(ppt, slide, sheetName + (pages.size() > 1 ? "  (page " + (p + 1) + " of " + pages.size() + ")" : ""));

                int tblRows = dataRowIdx.size() + (repeatHeaderRow ? 1 : 0);
                XSLFTable table = slide.createTable(tblRows, numCols);
                table.setAnchor(new Rectangle2D.Double(MARGIN_PT, MARGIN_PT + TITLE_HEIGHT_PT, availableWidth, availableTableHeight));

                int outRow = 0;
                if (repeatHeaderRow) {
                    writeRow(table, outRow++, rows.get(0), numCols);
                }
                for (int idx : dataRowIdx) {
                    writeRow(table, outRow++, rows.get(idx), numCols);
                }
                for (int c = 0; c < numCols; c++) {
                    table.setColumnWidth(c, colWidths[c]);
                }
                // Row heights: let each row keep its natural height; table's total
                // height comes from the sum, which is why pagination targets availableTableHeight.
                for (int r = 0; r < tblRows; r++) {
                    XSLFTableRow tr = table.getRows().get(r);
                    double h = (repeatHeaderRow && r == 0) ? rows.get(0).heightPt
                            : rows.get(dataRowIdx.get(r - (repeatHeaderRow ? 1 : 0))).heightPt;
                    tr.setHeight(Math.max(h, 10)); // guard against degenerate 0pt rows
                }
            }

            try (FileOutputStream out = new FileOutputStream(pptxPath)) {
                ppt.write(out);
            }
        }
    }

    // ---------- data extraction ----------

    private static List<RowSnapshot> readRows(Sheet sheet, FormulaEvaluator evaluator, DataFormatter formatter) {
        List<RowSnapshot> result = new ArrayList<>();
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();
        int numCols = 0;
        for (int r = firstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row != null) numCols = Math.max(numCols, row.getLastCellNum());
        }
        for (int r = firstRow; r <= lastRow; r++) {
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
            case CENTER: return TextAlign.CENTER;
            case RIGHT: return TextAlign.RIGHT;
            case JUSTIFY: return TextAlign.JUSTIFY;
            default: return TextAlign.LEFT;
        }
    }

    // ---------- layout ----------

    private static double[] computeColumnWidths(Sheet sheet, int numCols, double availableWidth) {
        double[] raw = new double[numCols];
        double sum = 0;
        for (int c = 0; c < numCols; c++) {
            // getColumnWidth is in 1/256ths of a character. This is an approximation
            // (assumes a Calibri-11-ish default font) -- good enough for layout purposes.
            int units = sheet.getColumnWidth(c);
            double px = (units / 256.0) * 7 + 5;
            raw[c] = px * 0.75; // px -> pt at 96dpi
            sum += raw[c];
        }
        double scale = sum > 0 ? availableWidth / sum : 1.0;
        double[] scaled = new double[numCols];
        for (int c = 0; c < numCols; c++) scaled[c] = raw[c] * scale;
        return scaled;
    }

    /** Groups row indices into pages so each page's total height fits availableTableHeight. */
    private static List<List<Integer>> paginate(List<RowSnapshot> rows, int headerRowIndex, double availableTableHeight, double headerHeight) {
        List<List<Integer>> pages = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        double currentHeight = headerHeight;

        int start = headerRowIndex == 0 ? 1 : 0;
        for (int i = start; i < rows.size(); i++) {
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

    private static void writeRow(XSLFTable table, int rowIdx, RowSnapshot rowSnap, int numCols) {
        XSLFTableRow tr = table.getRows().get(rowIdx);
        for (int c = 0; c < numCols; c++) {
            XSLFTableCell cell = tr.getCells().get(c);
            CellSnapshot cs = c < rowSnap.cells.size() ? rowSnap.cells.get(c) : new CellSnapshot();
            styleCell(cell, cs);
        }
    }

    private static void styleCell(XSLFTableCell cell, CellSnapshot cs) {
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
        run.setFontSize(cs.fontSize > 0 ? (double) cs.fontSize : 11.0);
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
        double fontSize = 11.0;
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