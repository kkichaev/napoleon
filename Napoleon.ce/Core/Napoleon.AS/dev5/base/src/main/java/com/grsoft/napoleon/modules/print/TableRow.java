package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.util.Vector;

import org.w3c.dom.Node;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import android.graphics.Canvas;
import android.graphics.Paint;

class TableRow extends Vector<TableCell> implements Parseable{
	private static final long serialVersionUID = 1L;
	private Table table;
	
	public TableRow(Table table){
		this.table = table;
	}
	
	@Override
	public void parse(Node node) {
		if (node != null && node.getFirstChild() != null &&
				node.getFirstChild().getNodeType() == Node.TEXT_NODE)
			add(TableCell.parseTableCell(table,
					node.getFirstChild().getNodeValue()));
	}
	
	public void draw(Canvas canvas, Paint paint, int rowIndex, int curY, DataSource source, int textHeight) {
		for(TableCell cell : this)
			cell.draw(canvas, paint, rowIndex, curY, source, textHeight);
	}
	
	public void drawPdf(Document doc, PdfWriter writer, int rowIndex, int curY, DataSource source, int textHeight) {
		for(TableCell cell : this)
			cell.drawPdf(doc, writer, rowIndex, curY, source, textHeight);
	}
	
	@Override
	public synchronized String toString() {
		return IterRepresentation.asString(this);
	}
}