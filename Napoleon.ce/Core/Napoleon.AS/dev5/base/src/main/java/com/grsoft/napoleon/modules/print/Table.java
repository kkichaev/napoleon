package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.util.List;
import org.w3c.dom.Element;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

class Table extends CellBase{
	private final String TAG = "Table";
	public static final String NAME = "Table";
	
	private String object;
	private VectorInteger widths;
	private VectorInteger hights;
	private int variableRow;
	private TableRows rows;
	private enum State { tsNone, tsPrintNextPage };
	private State state = State.tsNone;
	DataSource vRowSource;
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append(TAG).append(":\tobject: ").append(object).
			append(",\twidth: ").append(widths).append(",\thight: ").
			append(hights).append(",\tvariableRow: ").
			append(variableRow).append(",\trows: ").append(rows.toString());
		
		return result.toString();
	}

	@Override
	protected void readProperties(Element element) 
		throws PropNotFound, BadPropFormat {
		super.readProperties(element);
		
		object = (String) XmlElementParser.parse(element, "Object", String.class,
				null, "");
		widths = (VectorInteger) XmlElementParser
			.parse(element, "TableWidth", VectorInteger.class, null);
		hights = (VectorInteger) XmlElementParser
			.parse(element, "TableHeight", VectorInteger.class, null);
		variableRow = (Integer) XmlElementParser
			.parse(element, "VariableRow", int.class, null, -1);
		rows = (TableRows) XmlElementParser
			.parse(element, "Rows", TableRows.class, this);
		setFontSize((Integer) XmlElementParser
			.parse(element, "FontSize", int.class, null));
		
		Log.d(TAG, rows.toString());
	}

	@Override
	public boolean draw(Canvas canvas, Paint paint, DrawVisitor visitor, int idx) {
		Log.d(TAG, "draw");
		final int TOP_SHIFT = 50;
		
		if (state == State.tsPrintNextPage)
			visitor.shiftY -= getBounds().top - TOP_SHIFT;
		
		int top = drawRows(canvas, paint, 
				getBounds().top + visitor.shiftY, 0, variableRow, idx);
		
		if (variableRow == -1){
			visitor.maxY = top;
			return true;
		}
		
		visitor.freeSpaceHeight = getHeights().get(variableRow);
		
		if(variableRow != 0)
			visitor.freeSpaceHeight -= getHeights().get(variableRow - 1);
		
		visitor.freeSpaceHeight -= visitor.shiftY;
		visitor.top = top;
		
		Log.d("visitor top", Integer.toString(visitor.top));
		
		if (drawVRow(canvas, paint, visitor, false)){
			visitor.shiftY += (visitor.top - getHeights().get(variableRow));
			
			if( state != State.tsPrintNextPage )
		         visitor.shiftY -= getBounds().top;
		      else
		    	  visitor.shiftY -= TOP_SHIFT;
		
			Log.d("visitor top", Integer.toString(visitor.top));
			visitor.maxY = drawRows(canvas, paint, 
					visitor.top, variableRow + 1, -1, idx);		
			return true;
		}
		
		state = State.tsPrintNextPage;
		visitor.freeSpaceHeight =  visitor.pageHeight - visitor.top - tailHeight() - visitor.nextRowHeight;
		drawVRow(canvas, paint, visitor, true);
		visitor.maxY = drawRows(canvas, paint, visitor.top, variableRow + 1, -1, idx);
		vRowSource.startPage();
		return false;
	}
	
	@Override
	public boolean drawPdf(Document doc, PdfWriter writer, DrawVisitor visitor, int idx) {
		final int TOP_SHIFT = 150;
		
		if (state == State.tsPrintNextPage)
			visitor.shiftY -= getBounds().top - TOP_SHIFT;
		
		int top = drawPdfRows(doc, writer, getBounds().top + visitor.shiftY, 0, variableRow, idx);
		
		if (variableRow == -1){
			visitor.maxY = top;
			return true;
		}
		visitor.freeSpaceHeight = getHeights().get(variableRow);
		
		if(variableRow != 0)
			visitor.freeSpaceHeight -= getHeights().get(variableRow - 1);
		
		visitor.freeSpaceHeight -= visitor.shiftY;
		visitor.top = top;
		
		Log.d("visitor top", Integer.toString(visitor.top));
		
		if (drawPdfVRow(doc, writer, visitor, false)){
			visitor.shiftY += (visitor.top - getHeights().get(variableRow));
			
			if( state != State.tsPrintNextPage )
		         visitor.shiftY -= getBounds().top;
		      else
		    	  visitor.shiftY -= TOP_SHIFT;
		
			Log.d("visitor top", Integer.toString(visitor.top));
			visitor.maxY = drawPdfRows(doc, writer, visitor.top, variableRow + 1, -1, idx);		
			return true;
		}
		
		state = State.tsPrintNextPage;
		visitor.freeSpaceHeight =  visitor.pageHeight - visitor.top - tailHeight() - visitor.nextRowHeight;
		drawPdfVRow(doc, writer, visitor, true);
		visitor.maxY = drawPdfRows(doc, writer, visitor.top, variableRow + 1, -1, idx);
		doc.newPage();
		vRowSource.startPage();
		return false;
	}
	
	private boolean drawPdfVRow(Document doc, PdfWriter writer, DrawVisitor visitor, boolean checkData) {
		TableRow row = rows.get(variableRow);
		
		while(true){
			if( checkData &&  vRowSource != null && vRowSource.haveMoreData() == false)
				return false;
			
			int ch = rowTextHeight(writer, variableRow);
			visitor.nextRowHeight = ch;
			if(ch > visitor.freeSpaceHeight)
				return false;
			
			if(vRowSource != null)
				vRowSource.calculate();
			
			row.drawPdf(doc, writer, variableRow, visitor.top, vRowSource, ch);
			visitor.top += ch;
			visitor.freeSpaceHeight -= ch;
			
			if(vRowSource == null || !vRowSource.moveNext())
				return true;
		}
	}

	private int rowTextHeight(PdfWriter writer, int vr) {
		final int Y_OFFSET = 6;
		int textHeight = 0;
		
		for(TableCell cell : rows.get(vr)){
			int hgh = cell.getTextHeight(writer, vRowSource);
			if(textHeight < hgh)
				textHeight = hgh;
		}
		
		return textHeight + Y_OFFSET;
	}

	int drawPdfRows(Document doc, PdfWriter writer, int top, int startRow, int endRow, int idx) {
		Log.d(TAG, "drawRaws");
		
		if (startRow >= rows.size() || startRow < 0)
			return top;
		
		int curY = top;
		
		for (int i = startRow; i < rows.size() && i != endRow; i++) {
			TableRow row = rows.get(i);
			row.drawPdf(doc, writer, i, curY, source.get(idx), -1);
			
			int dh = getHeights().get(i);
			if (i > 0)
				dh -= getHeights().get(i - 1);
			
			curY += dh;
		}
		
		return curY;
	}
	
	private int tailHeight() {
		   if( variableRow == -1 || variableRow == rows.size()-1 ) return 0;
		   int h = getHeights().get(getHeights().size()-1);
		   h -= getHeights().get(variableRow);

		   return h;
	}

	private boolean drawVRow(Canvas canvas, Paint paint, DrawVisitor visitor, 
			boolean checkData){
		TableRow row = rows.get(variableRow);
		
		while(true){
			if( checkData &&  vRowSource != null && vRowSource.haveMoreData() == false)
				return false;
			
			int ch = rowTextHeight(paint, variableRow);
			visitor.nextRowHeight = ch;
			if(ch > visitor.freeSpaceHeight)
				return false;
			
			if(vRowSource != null)
				vRowSource.calculate();
			
			row.draw(canvas, paint, variableRow, visitor.top, vRowSource, ch);
			visitor.top += ch;
			visitor.freeSpaceHeight -= ch;
			
			if(vRowSource == null || !vRowSource.moveNext())
				return true;
		}
	}
	
	private int rowTextHeight(Paint paint, int vr) {
		final int Y_OFFSET = 6;
		int textHeight = 0;
		
		for(TableCell cell : rows.get(vr)){
			int hgh = cell.getTextHeight(paint, vRowSource);
			if(textHeight < hgh)
				textHeight = hgh;
		}
		
		return textHeight + Y_OFFSET;
	}

	private int drawRows(Canvas canvas, Paint paint, int top, int startRow, int endRow, int idx)
	{
		Log.d(TAG, "drawRaws");
		
		if (startRow >= rows.size() || startRow < 0)
			return top;
		
		int curY = top;
		
		for (int i = startRow; i < rows.size() && i != endRow; i++){
			TableRow row = rows.get(i);
			row.draw(canvas, paint, i, curY, source.get(idx), -1);
			
			int dh = getHeights().get(i);
			if (i > 0)
				dh -= getHeights().get(i - 1);
			
			curY += dh;
		}
		
		return curY;
	}
	
	public int getVariableRow(){
		return variableRow;
	}
	
	public VectorInteger getHeights(){
		return hights;
	}
	
	public VectorInteger getWidths(){
		return widths;
	}
	
	@Override
	public void beforePrint(List<DataSource> source, int idx) {
		if (object != null && object.length() > 0)
			this.vRowSource = source.get(idx).getObject(object);
		else
			this.vRowSource = source.get(idx);
		
		this.source = source;
	}
}
