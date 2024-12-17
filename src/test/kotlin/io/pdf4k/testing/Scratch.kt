package io.pdf4k.testing

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.awt.Color
import java.io.FileOutputStream
import java.nio.file.Path

fun main() {
    val path = Path.of("./fontsize.pdf")
    FileOutputStream(path.toFile()).also { output ->
        Document().use { document ->
            PdfWriter.getInstance(document, output)
            document.open()
            val red = FontFactory.getFont("arial unicode ms", 16f, Font.BOLD, Color.RED)
            val green = FontFactory.getFont("arial unicode ms", 24f, Font.BOLD, Color.GREEN)
            val blue = FontFactory.getFont("arial unicode ms", 16f, Font.BOLD, Color.BLUE)
            val p = Paragraph().also {
                it.setLeading(0f, 1.2f)
                it.add(Phrase(32F, "jdf osi hfoh dfoihsd pjpojpo jpoj poj pj po jpoj poj poj pojpoj poj ;oj poj poj po jpoj po jpo jpo jpoj poj po jp oif hoisdhf o sdh ofighs doifugosi dufg oisdu gfoiusg dfoiusg dofiugs dofiugsd oifugs oidfug osidufg oidsugf oiudsgf oisudg foisudg foisugf"))
                it.add(Phrase().also {
                    it.add(Chunk(" RED", red))
                })
                it.add(Phrase(42f, " GREEN", green))
                it.add(Phrase(" BLUE", blue))
                it.add(Phrase(" jdf osi hfoh dfoihsd pjpojpo jpoj poj pj po jpoj poj poj pojpoj poj ;oj poj poj po jpoj po jpo jpo jpoj poj po jp oif hoisdhf o sdh ofighs doifugosi dufg oisdu gfoiusg dfoiusg dofiugs dofiugsd oifugs oidfug osidufg oidsugf oiudsgf oisudg foisudg foisugf"))
            }
            document.add(p)
            val table = PdfPTable(1)
            table.defaultCell.setLeading(0f,1.2f)
            table.addCell(Paragraph().also {
                it.add(Chunk("jdf osi hfoh dfoihsd pjpojpo jpoj poj pj po jpoj poj poj pojpoj poj ;oj poj poj po jpoj po jpo jpo jpoj poj po jp oif hoisdhf o sdh ofighs doifugosi dufg oisdu gfoiusg dfoiusg dofiugs dofiugsd oifugs oidfug osidufg oidsugf oiudsgf oisudg foisudg foisugf"))
                it.add(Chunk(" RED", red))
                it.add(Chunk(" GREEN", green))
                it.add(Chunk(" BLUE", blue))
                it.add(Chunk(" jdf osi hfoh dfoihsd pjpojpo jpoj poj pj po jpoj poj poj pojpoj poj ;oj poj poj po jpoj po jpo jpo jpoj poj po jp oif hoisdhf o sdh ofighs doifugosi dufg oisdu gfoiusg dfoiusg dofiugs dofiugsd oifugs oidfug osidufg oidsugf oiudsgf oisudg foisudg foisugf"))
            })
            table.setWidths(floatArrayOf(1f))
            table.widthPercentage = 100.0f
            document.add(table)
        }
    }.also { it.close() }
}
