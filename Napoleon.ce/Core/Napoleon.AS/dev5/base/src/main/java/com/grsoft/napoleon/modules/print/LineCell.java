package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

class LineCell extends CellBase{
	private final String TAG = "LineCell";
	static final int FONT_SIZE = 5;
	public static final String NAME = "Line";
	private String lineText = "";
	private boolean horizontal = false;
	
	@Override
	public String toString() {
		return TAG + " lineText: " + lineText +
		" horizontal: " + Boolean.toString(horizontal) + 
		" size: " + size.toString() + " location: " + location.toString();
	}
	
	@Override
	protected void readProperties(Element element) 
	throws PropNotFound, BadPropFormat {
		super.readProperties(element);
		
		lineText = (String) XmlElementParser
			.parse(element, "LineText", String.class, null, "");
		horizontal = (Boolean) XmlElementParser
			.parse(element, "Horizontal", boolean.class, null);
	}

	@Override
	public boolean draw(Canvas canvas, Paint paint, DrawVisitor visitor, int idx) {
		Log.d(TAG, "draw");
		location.valY += visitor.shiftY;

		if (horizontal){
			float oldTextSize = paint.getTextSize();
			Paint.Align oldAlign = paint.getTextAlign();
			
			canvas.drawLine(location.valX, location.valY, 
					location.valX + size.valX, location.valY, paint);
			final int UNDERLINE_FONT_SIZE = 22 / NPrinter.ZOOM_SCALE;
			paint.setTextSize(UNDERLINE_FONT_SIZE);
			
			if (lineText.length() > 0){
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(lineText, location.valX + size.valX / 2, 
						location.valY + UNDERLINE_FONT_SIZE, paint);
			}
			
			paint.setTextSize(oldTextSize);
			paint.setTextAlign(oldAlign);
		}else
			canvas.drawLine(location.valX, location.valY, 
					location.valX, location.valY + size.valY, paint);
		
		return true;
	}

	@Override
	public boolean drawPdf(com.itextpdf.text.Document doc, PdfWriter writer, DrawVisitor visitor, int idx) {
		try {
			PdfPTable t = new PdfPTable(1);
			String txt = new String(lineText.getBytes("cp1251"), "cp1251");
			PdfPCell c = new PdfPCell(new Phrase(
					txt, 
					new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), FONT_SIZE)));

			c.setBorder(horizontal ? PdfPCell.TOP : PdfPCell.LEFT);
			c.setFixedHeight((horizontal) ? 10 : (float)size.valY * PrintForm.SCALE_COEF);
			c.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			t.setTotalWidth((float)size.valX * PrintForm.SCALE_COEF);
			t.addCell(c);
			
			location.valY += visitor.shiftY;

			PrintForm.drawTable(t, doc, writer, location.valX, location.valY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
