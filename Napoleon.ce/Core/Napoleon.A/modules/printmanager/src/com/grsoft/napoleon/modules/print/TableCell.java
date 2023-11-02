package com.grsoft.napoleon.modules.print;

import java.io.StringReader;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;

class TableCell{
	
	private static final String TAG = "TableCell"; 
	private int index;
	private int colspan;
	private int rowspan;
	private String text;
	private Align alignt = new Align();
	private Table table;
	private CellBorder border = new CellBorder();
	Image cellImage;
	boolean isBarCode = false;
	
	public TableCell(Table table){
		this.table = table;
	}
	
	public static TableCell parseTableCell(Table parent, String value){
		TableCell result = new TableCell(parent);
		String[] str = value.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		result.index = Integer.parseInt(str[0]);
		result.colspan = Integer.parseInt(str[1]);
		result.rowspan = Integer.parseInt(str[2]);
		
		if (str[3].startsWith("\"") && str[3].endsWith("\""))  
			str[3] = str[3].substring(1, str[3].length() - 1);
		
		result.text = str[3];
		
		if (str.length > 4){
			String abStr = str[4];
			int bp = abStr.indexOf("-");
			result.alignt = Align.parseAlign(abStr.substring(0, bp == -1 ? abStr.length() : bp));
			
			if(bp != -1)
				result.border = CellBorder.parseBorder(abStr.substring(bp + 1));
		}
		
		return result;
	}
	
	public void draw(Canvas canvas, Paint paint, int rowIndex, int curY, DataSource source, int textHeight) {
		Log.d(TAG, "text: " + text + " draw: " + rowIndex + " curY: " + curY);

		if( textHeight == -1 )
			textHeight = getHeight(rowIndex);
		
		Rect rect = new Rect();
		rect.left = index == 0 ? 0 : table.getWidths().get(index - 1);
		rect.left += table.getLocation().valX;
		rect.top = curY;
		rect.right = rect.left + getWidth() + 1;
		rect.bottom = curY + textHeight;
		
		paint.setTextSize(table.getFontSize());
		
		Painter.paintText(canvas, paint, 
				getPrintText(source), 
				getTextHeight(paint, source), rect, 
				new Dimension(3 / NPrinter.ZOOM_SCALE, 2 / NPrinter.ZOOM_SCALE), alignt);
		
		if (!border.isNone()){
			Paint.Style oldStyle = paint.getStyle();
			paint.setStyle(Paint.Style.STROKE);
			
			if (border.isAll())
				canvas.drawRect(rect, paint);
			else {
				if (border.isTop())
					canvas.drawLine(rect.left, rect.top, 
							rect.right, rect.top, paint);
				if (border.isLeft())
					canvas.drawLine(rect.left, rect.top, 
							rect.left, rect.bottom, paint);
				if (border.isRight())
					canvas.drawLine(rect.right, rect.top, 
							rect.right, rect.bottom, paint);
				if (border.isBottom())
					canvas.drawLine(rect.left, rect.bottom, 
							rect.right, rect.bottom, paint);
			}
			
			paint.setStyle(oldStyle);
		}
	}
	
	private int getWidth(){
		VectorInteger widths = table.getWidths(); 
		int result = widths.get(index + colspan - 1);
		
		if(index != 0)
			result -= widths.get(index - 1);
		
		return result;
	}
	
	private int getHeight(int rowIndex){
		VectorInteger heights = table.getHeights(); 
		int result = heights.get(rowIndex + rowspan - 1);
		
		if (rowIndex != 0)
			result -= heights.get(rowIndex - 1);
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append('[').
			append(index).append(", ").
			append(colspan).append(", ").
			append(rowspan).append(", ").
			append(text).append(", ").
			append(alignt.toString()).
			append(']');
		
		return result.toString();
	}
	
	public String getPrintText(DataSource source){
		return CellTextFormatter.format(text, source);
	}
	
	public int getTextHeight(Paint paint, DataSource source){
		TextPaint tp = new TextPaint(paint);

		Alignment alignment;

		if (alignt.isRight())
			alignment = Alignment.ALIGN_OPPOSITE;
		else if (alignt.isCenter())
			alignment = Alignment.ALIGN_CENTER;
		else
			alignment = Alignment.ALIGN_NORMAL;
		
		String text = getPrintText(source);
		StaticLayout sl = new StaticLayout(text, 
				tp, getWidth(), 
				alignment, 
				1.0f, 
				0.0f, true);
		
		return sl.getHeight();
	}
	
	public int getTextHeight(PdfWriter writer, DataSource source) {
		cellImage = CellTextFormatter.getImage(text, source, writer);
		if(cellImage != null) {
			isBarCode = text.startsWith(NPrinter.BC_MARKER);
			int hgh = (int) (cellImage.getScaledHeight() / PrintForm.SCALE_COEF);
//			if(!isBarCode)
//				hgh += IMAGE_PADDING * 2;
			return hgh;
		}
		
		PdfContentByte canvas = writer.getDirectContent(); 
		ColumnText ct = new ColumnText(canvas);
		float left = (index == 0 ? 0 : table.getWidths().get(index - 1)) * PrintForm.SCALE_COEF;
		float right = left + (getWidth() + 1) * PrintForm.SCALE_COEF;
		float top = table.getBounds().top * PrintForm.SCALE_COEF;
		float bottom = table.getBounds().bottom * PrintForm.SCALE_COEF;
		float leftColumn[] = { left, top, left, bottom };
		float rightColumn[] = { right, top, right, bottom };
		
		ct.setColumns(leftColumn, rightColumn);
		Phrase p = getPhrase(source);
		ct.setText(p);
		try {
			ct.go(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		float hgh = p.getFont().getBaseFont().getFontDescriptor(BaseFont.ASCENT, p.getFont().getSize());
		hgh -= p.getFont().getBaseFont().getFontDescriptor(BaseFont.DESCENT, p.getFont().getSize());
		int textHeight = (int)(ct.getLinesWritten() * (hgh + 2) / PrintForm.SCALE_COEF);
		return textHeight;
	}
	
	@SuppressLint("DefaultLocale")
	Phrase getPhrase(DataSource source) {
		Phrase p = null;		
		try {
			String txt = new String(getPrintText(source).getBytes("cp1251"), "cp1251");
			float fontSize = table.getFontSize() / (CellBase.FONT_SCALE * 1.2f); 
			if(txt.contains("</")) {
				p = new Phrase("",  new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), fontSize));
				StringReader sr = new StringReader(txt);
				List<Element> els = HTMLWorker.parseToList(sr, null);
				for(Element el : els) {
					p.add(el);
				}
			} else {
				p = new Phrase(
				txt, 
				new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 
						fontSize));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return p;
	}

	public void drawPdf(Document doc, PdfWriter writer, int rowIndex, int curY, DataSource source, int textHeight) {
		if( textHeight == -1 )
			textHeight = getHeight(rowIndex);
		
		Rect rect = new Rect();
		rect.left = index == 0 ? 0 : table.getWidths().get(index - 1);
		rect.left += table.getLocation().valX;
		rect.top = curY;
		rect.right = rect.left + getWidth() + 1;
		rect.bottom = curY + textHeight;
	
		try {
			PdfPTable t = new PdfPTable(1);
			PdfPCell c;
			if(cellImage != null) {
				float height = (float)rect.height() * PrintForm.SCALE_COEF;
				if(isBarCode) {
					int hgh = textHeight;
					cellImage.scaleAbsolute(((float)rect.width() - 8*2)* PrintForm.SCALE_COEF, hgh * PrintForm.SCALE_COEF);
				} else {
					float imgHeight = cellImage.getScaledHeight() + 2 * NPrinter.IMAGE_PADDING * PrintForm.SCALE_COEF;
					if(imgHeight > height) {
						float coef = height / imgHeight;
						cellImage.scaleAbsolute(cellImage.getScaledWidth() * coef, cellImage.getScaledHeight() * coef);
					}
				}
				
				c = new PdfPCell(cellImage);
				c.setFixedHeight(height);
			} else {
				c = new PdfPCell(getPhrase(source));

				c.setPaddingTop(0);
				c.setPaddingBottom(alignt.isBottom() ? 3 :  0);
				c.setPaddingLeft(1);
				c.setPaddingRight(1);
				c.setFixedHeight((float)rect.height() * PrintForm.SCALE_COEF);
			}

			alignt.setAlign(c);
			c.setBorder(PdfPCell.NO_BORDER);
			border.setBorder(c);
			t.setTotalWidth((float)rect.width() * PrintForm.SCALE_COEF);
			t.addCell(c);
			
			PrintForm.drawTable(t, doc, writer, rect.left, rect.top);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
