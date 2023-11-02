package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.io.InputStream;
import org.w3c.dom.Element;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;


public class PictureCell extends CellBase {
	public static final String NAME = "Picture";
	private Image image;
	private Context context;
	
	public PictureCell(Context context) {
		this.context = context;
	}

	@Override
	protected void readProperties(Element element) throws PropNotFound, BadPropFormat {
		super.readProperties(element);

		InputStream is = null;
		
		try {
			String name = (String) XmlElementParser
					.parse(element, "Image", String.class, null);
			name = name.substring(0, name.indexOf("."));
			Resources res = context.getResources();
			int id = res.getIdentifier(name, "raw", context.getPackageName());
			is = context.getResources().openRawResource(id);
			image = PngImage.getImage(is);
			image.scaleAbsolute((float)size.valX * PrintForm.SCALE_COEF, (float)size.valY * PrintForm.SCALE_COEF);
		} catch (Exception e) {	e.printStackTrace();
		} finally{
			if (is != null)	try { is.close(); } catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	@Override
	public boolean draw(Canvas canvas, Paint paint, DrawVisitor visitor, int idx) {
		return false;
	}
	
	@Override
	public boolean drawPdf(Document doc, PdfWriter writer, DrawVisitor visitor, int idx) {
		try{
			location.valY += visitor.shiftY;
			float xPos = (float)location.valX;
			image.setAbsolutePosition(xPos  * PrintForm.SCALE_COEF + doc.leftMargin(), 
					doc.getPageSize().getHeight() - (float)location.valY * PrintForm.SCALE_COEF - 
						(float)size.valY * PrintForm.SCALE_COEF);
			doc.add(image);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
