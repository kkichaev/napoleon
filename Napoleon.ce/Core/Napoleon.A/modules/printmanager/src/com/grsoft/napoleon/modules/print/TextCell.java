package com.grsoft.napoleon.modules.print;

import java.io.StringReader;
import java.util.List;

import org.w3c.dom.Element;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

class TextCell extends CellBase{
	private static final String TAG = "TextCell";
	public static final String NAME = "Label";
	
	private String text;
	private Align align;

	@Override
	public String toString() {
		return TAG + " text: " + text +
		" fontSize: " + getFontSize() + " align: " + align.toString() +
		" size: " + size.toString() + " location: " + location.toString();
	}


	@Override
	protected void readProperties(Element element) 
		throws PropNotFound, BadPropFormat {
		super.readProperties(element);

		setFontSize((Integer) XmlElementParser
			.parse(element, "FontSize", int.class, null));
		
		text = (String) XmlElementParser
			.parse(element, "Text", String.class, null);
		align = (Align) XmlElementParser
			.parse(element, "TextAlign", Align.class, null);
	}


	@Override
	public boolean draw(Canvas canvas, Paint paint, DrawVisitor visitor, int idx) {
		Log.d(TAG, "draw text: " + text + " fontSize: " + getFontSize());
		
		paint.setTextSize(getFontSize());
		location.valY += visitor.shiftY;
		
		Painter.paintText(canvas, paint, 
				CellTextFormatter.format(text, source.get(idx)), (int)getFontSize(), 
				getBounds(), new Dimension(0,0), align);
		
		return true;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public boolean drawPdf(com.itextpdf.text.Document doc, PdfWriter writer, DrawVisitor visitor, int idx) {
		try {
			PdfPTable t = new PdfPTable(1);
			DataSource ds = source.get(idx);
			PdfPCell c;
			Image bc = CellTextFormatter.getImage(text, ds, writer);
			if(bc != null)
				c = new PdfPCell(bc);
			else {
				String txt = new String(CellTextFormatter.format(text, ds).getBytes("cp1251"), "cp1251");
				if(txt.contains("</")) {
					StringReader sr = new StringReader(txt);
					Phrase p = new Phrase("");//, new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), getFontSize() / (FONT_SCALE * 1.2f)));
					List<com.itextpdf.text.Element> els = HTMLWorker.parseToList(sr, null);
					for(com.itextpdf.text.Element el : els) {
						p.add(el);
					}
					c = new PdfPCell(p);
				} else {
					c = new PdfPCell(new Phrase(
					txt, 
					new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), getFontSize() / (FONT_SCALE * 1.2f))));
				}
			}
			c.setBorder(PdfPCell.NO_BORDER);
			c.setFixedHeight((float)size.valY * PrintForm.SCALE_COEF * 1.2f);
			align.setAlign(c);
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
