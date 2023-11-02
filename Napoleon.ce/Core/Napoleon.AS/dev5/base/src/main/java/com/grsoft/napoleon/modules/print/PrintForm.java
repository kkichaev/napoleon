package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

class PrintForm{
	public static final int PAGE_HEIGHT_PX = 3320;
	public static final int PAGE_WIDTH_PX = 2400;
	public static final String FONT_NAME = "/system/fonts/DroidSans.ttf";
	static final String HEADER_NODE = "Header";
	/**
	 * перевод из пискелов в units (point)
	 */
	public static final float SCALE_COEF = 0.23f;

	private final String TAG = "PrintForm"; 
	private List<CellBase> cells = new ArrayList<CellBase>(); 
	List<CellBase> header = new ArrayList<CellBase>();
	private boolean album;
	private int width;
	private int height;
	private int leftMargin;
	private List<DataSource> source;
	
	public PrintForm(List<DataSource> source, Dimension dim){
		this.source = source;
		width = dim.valX;
		height = dim.valY;
	}

	public int getWidth(){
		return album ? height : width;
	}
	
	public int getHeight(){
		return album ? width : height;
	}
	
	public boolean build(Context context, int rId){
		try{
			Document dom = DocumentBuilderFactory
				.newInstance().newDocumentBuilder()
				.parse(context.getResources().openRawResource(rId));
			
			if (dom.getChildNodes().getLength() != 1)
				throw new InvalidFormFormat();
			
			NodeList nodeList = dom.getChildNodes().item(0).getChildNodes();
			
			album = (Boolean) XmlElementParser.parse(dom.getDocumentElement(), "Album", boolean.class, null, false);
			leftMargin = (Integer) XmlElementParser.parse(dom.getDocumentElement(), "LeftMargin", int.class, null, 0);
		
			cells.clear();
			header.clear();
			
			for (int i = 0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE){
					try{
						if(node.getNodeName().equals(HEADER_NODE)) {
							NodeList headerList = node.getChildNodes(); 
							for(int hi = 0; hi<headerList.getLength(); hi++) {
								Node chNode = headerList.item(hi);
								CellBase cell = CellBase.create(context, chNode, source, 0);
								
								if (cell != null){
									header.add(cell);
									Log.d(TAG, "Header added: " + cell.toString());
								}
							}
						} else {
							CellBase cell = CellBase.create(context, node, source, 0);
							
							if (cell != null){
								cells.add(cell);
								Log.d(TAG, "Added: " + cell.toString());
							}
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			Collections.sort(cells, new CellCmp());
			Collections.sort(header, new CellCmp());
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public File drawPdf(Context context, File cacheDir, List<Integer> reports) {
		File result = null;
		try {			
			result = new File(cacheDir, "out.pdf");
			
			// A4 size
			com.itextpdf.text.Document doc = null;
			PdfWriter writer = null; 
			
			for(int srcId = 0; srcId < source.size(); srcId++)
				for(int resId : reports) {
					if( !build(context, resId) )
						continue;
					
					if(doc == null){
						doc = new com.itextpdf.text.Document((album) ? 
								PageSize.A4.rotate() : 
								PageSize.A4);
						writer = PdfWriter.getInstance(doc, new FileOutputStream(result));
						doc.open();
					}else {
						doc.setPageSize((album) ? 
								PageSize.A4.rotate() : 
								PageSize.A4);
						doc.newPage();
					}
					
					doc.setMargins(leftMargin, 0, 0, 0);
					
					if(source != null) {
						DataSource s = source.get(srcId);
						s.init(context, resId);
						s.startPage();
					}
											
					DrawVisitor visitor = new DrawVisitor();
					visitor.pageHeight = getHeight();
					for ( int i=0; i < cells.size(); ){
						visitor.maxY = height;
						
						CellBase cell = cells.get(i);
						
						if (!cell.drawPdf(doc, writer, visitor, srcId)){
							visitor.maxY = height;
							visitor.freeSpaceHeight = 0;
							visitor.shiftY = 0;
							visitor.top = 0;
							System.gc();
							
							drawPdfHeader(doc, writer, srcId);
						}else
							i++;
					}
				}
	
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	void drawPdfHeader(com.itextpdf.text.Document doc, PdfWriter writer, int srcId) {
		DrawVisitor visitor = new DrawVisitor();
		visitor.pageHeight = getHeight();
		
		for ( int i=0; i < header.size(); ){
			visitor.maxY = height;
			CellBase cell = header.get(i);
			
			if (!cell.drawPdf(doc, writer, visitor, srcId)){
				visitor.maxY = height;
				visitor.freeSpaceHeight = 0;
				visitor.shiftY = 0;
				visitor.top = 0;
				System.gc();
			}else
				i++;
		}
	}
	
	public static void drawTable(PdfPTable table, com.itextpdf.text.Document doc, PdfWriter writer, int left, int top) {
		PdfContentByte canvas = writer.getDirectContent();
		
		table.writeSelectedRows(0, -1, 
			left * PrintForm.SCALE_COEF + doc.leftMargin(), 
			doc.getPageSize().getHeight() - top * PrintForm.SCALE_COEF, canvas);
	}
	
	public List<File> draw(Context context, File cacheDir){
		System.gc();
		System.runFinalization(); 
		System.gc();
		List<File> result = new ArrayList<File>();
		
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), 
				getHeight(), Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap); 
		canvas.drawColor(Color.WHITE);
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(1.0f);
		paint.setColor(Color.BLACK);
		
		DrawVisitor visitor = new DrawVisitor();
		visitor.pageHeight = getHeight();
		
		if(source != null)
			source.get(0).startPage();
		
		int pageIndex = 1;
		int i = 0;
		
		for ( ; i < cells.size(); ){
			visitor.maxY = height;
			
			CellBase cell = cells.get(i);
			
			if (!cell.draw(canvas, paint, visitor, 0)){
				visitor.maxY = height;
				visitor.freeSpaceHeight = 0;
				visitor.shiftY = 0;
				visitor.top = 0;
				System.gc();
				result.add(save(context, bitmap, pageIndex++, cacheDir));
				clear(canvas, paint);
				drawHeader(canvas, paint, 0);
			}else
				i++;
		}
		
		result.add(save(context, bitmap, pageIndex, cacheDir));
		bitmap.recycle();
		bitmap = null;
		canvas = null;
		paint = null;
		System.gc();
		System.runFinalization(); 
		System.gc();
		return result;
	}
	
	void drawHeader(Canvas canvas, Paint paint, int srcId) {
		DrawVisitor visitor = new DrawVisitor();
		visitor.pageHeight = getHeight();
		
		for ( int i =0; i < header.size(); ){
			visitor.maxY = height;
			
			CellBase cell = header.get(i);
			
			if (!cell.draw(canvas, paint, visitor, 0)){
				visitor.maxY = height;
				visitor.freeSpaceHeight = 0;
				visitor.shiftY = 0;
				visitor.top = 0;
				System.gc();
			}else
				i++;
		}
	}

	private void clear(Canvas canvas, Paint paint) {
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
	    paint.setColor(Color.BLACK);
	}

	private File save(Context context, Bitmap bitmap, int pageIndex, File cacheDir) {
		String pageName = "p" + pageIndex + ".png";
		
		File result = new File(cacheDir, pageName);
		
		try{
			OutputStream os = new BufferedOutputStream(new FileOutputStream(result));
			bitmap.compress(CompressFormat.PNG, 70, os);
			os.flush();
			os.close();
		} catch(Exception e){
			
		}
		
		return result;
	}
}
