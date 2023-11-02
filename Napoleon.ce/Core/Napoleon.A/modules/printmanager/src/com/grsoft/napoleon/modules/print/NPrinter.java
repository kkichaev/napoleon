package com.grsoft.napoleon.modules.print;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.TextPrinterSetting;
import com.grsoft.napoleon.Setting;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

public class NPrinter {
	public final static String BC_MARKER = "@barcode";
	public final static String IMAGE_MARKER = "@image";
	public static int IMAGE_PADDING = 10;
	public static int BARCODE_HEIGHT = 100;

	@SuppressWarnings("unused")
	private static final String TAG = "Printer";
	private static GraphicPrinter printStrategy = new GraphicPrinter();
	private static Map<String, Class<? extends GraphicPrinter>> strategies =
			new HashMap<String, Class<? extends GraphicPrinter>>();
	public final static String SEND_TXT_FILE_ACTION = "com.grsoft.napoleon.modules.TextPrinter.sendPrintTask";
	
	/***
	 * Стратегия для печати на графическом принтере
	 */
	public static String GRAPHICS = "GraphicPrinter";
	
	/***
	 * Стратегия для печати на текстовом принтере
	 */
	public static String TEXT = "TextPrinter";
	
	/***
	 * Таблица печатный форм. Формат: заголовок - ресурс
	 */
	public static Map<String, String> forms = 
			new HashMap<String, String>();
	
	/***
	 * Все размеры будут уменьшаться в SIZE_SCALE раз  
	 */
	public static int ZOOM_SCALE = Features.PRINT_THROW_PDF ? 1 : 2;
	public static String TORG_12_CAPTION = "ТТН ТОРГ 12";
	public static String TORG_12_NAME = "torg12";
	public static String SCHET_FACT_CAPTION = "Счет-фактура";
	public static String SCHET_FACT_NAME = "schf";
	public static String PA_CAPTION = "pa";
	public static String PA_NAME = "m2";
	public static String PKO_CAPTION = "pko";
	public static String PKO_NAME = "pko";
	public static String VAN_REST_CAPTION = "vanrest";
	public static String VAN_REST_NAME = "vanrest";
	public static String UPD_CAPTION = "УПД";
	public static String UPD_NAME = "upd";
	
	static{
		forms.put(TORG_12_CAPTION, TORG_12_NAME);
		forms.put(SCHET_FACT_CAPTION, SCHET_FACT_NAME);
		forms.put(PA_CAPTION, PA_NAME);
		forms.put(PKO_CAPTION, PKO_NAME);
		forms.put(VAN_REST_CAPTION, VAN_REST_NAME);
		forms.put(UPD_CAPTION, UPD_NAME);
		
		strategies.put(GRAPHICS, GraphicPrinter.class);
		strategies.put(TEXT, TextPrinter.class);
	}
	
	public static File print(Context context, String report, DataSource source){
		List<DataSource> list = new ArrayList<DataSource>();
		list.add(source);
		return print(context, report, list);
	}
	
	public static File print(Context context, String report, List<DataSource> source){
		return printStrategy.print(context, report, source);
	}
	
	public static void sendPrintTask(Context context, File file){
		printStrategy.sendPrintTask(context, file);
	}
	
	public static boolean setPrintStrategy(String name){
		boolean result = false;
		
		if (strategies.containsKey(name)){
			Class<? extends GraphicPrinter> strategyType = strategies.get(name);
			
			if (strategyType != printStrategy.getClass()){
				try{
					printStrategy = strategyType.newInstance();
					boolean addSettings = (printStrategy instanceof TextPrinter);
					if( addSettings ) {
						if(!Setting.addTabs.contains(TextPrinterSetting.class))
							Setting.addTabs.add(TextPrinterSetting.class);
					} else {
						Setting.addTabs.remove(TextPrinterSetting.class);
					}
					result = true;
				}catch(Exception e){
					e.printStackTrace();
					result = false;
				}
			}
		}
		
		return result;
	}
}

class InvalidFormFormat extends Exception{
	private static final long serialVersionUID = -6437884825005475615L;}

class InvalidNodeName extends Exception{
	private static final long serialVersionUID = 5452018753156283329L;
	public InvalidNodeName(String name){
		super(String.format("Invalid node name", name));
	}
}

/***
 * Исключение возникает когда
 * свойство, обязательное для объекта не было найдено в 
 * XML файле
 * @author kki
 *
 */
class PropNotFound extends Exception{
	private static final long serialVersionUID = -7079013284919571452L;
	public PropNotFound(String name) {
		super(String.format("Node not found: %s", name));
	}
}

class CellCmp implements Comparator<CellBase>{

	@Override
	public int compare(CellBase object1, CellBase object2) {
		Dimension loc1 = object1.getLocation();
		Dimension loc2 = object2.getLocation();
		
		int val = loc1.valY - loc2.valY;
		
		if (val == 0)
			val = loc1.valX - loc2.valX;
		
		return val;
	}
	
}

class XmlElementParser{
	/**
	 * Читает значение елемента в тип поля
	 * ожидаемое объектом.
	 * 
	 * Поле обязательно должно придти быть в <code>element</code>,
	 * и имеет ожидаемый объектом тип, иначе генерируется исключение
	 * 
	 * @param element текущий XML
	 * @param name имя поля
	 * @param type ожидаемый тип
	 * @return
	 * @throws PropNotFound поле не найдено в XML элементе
	 * @throws BadPropFormat ошибка при преобразовании формата XML в
	 * ожидаемый тип данных <code>type</code>
	 */
	public static Object parse(Element element, String name, Class<?> type, Object parent) throws PropNotFound, BadPropFormat{
		NodeList nl = element.getElementsByTagName(name);
		
		if(nl == null)
			throw new PropNotFound(name);
		
		if (nl != null && nl.getLength() == 1){
			Node node = nl.item(0);
			
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE){
				if (type == VectorInteger.class)
					return parse(node, "Item", new VectorInteger());
				
				Node valueNode = node.getFirstChild();
				
				if (valueNode != null){
					if (type == int.class)
						return Integer.parseInt(valueNode.getNodeValue());
					else if (type == String.class)
						return valueNode.getNodeValue();
					else if (type == Align.class)
						return Align.parseAlign(valueNode.getNodeValue());
					else if (type == boolean.class)
						return Boolean.parseBoolean(valueNode.getNodeValue());
					else if (type == Dimension.class)
						return Dimension.parseDimension(valueNode.getNodeValue());
					else if (type == TableRows.class)
						return parse(node, "Cells", 
								new TableRows((Table) parent));
				}
			}
		}
		
		throw new BadPropFormat(name);
	}
	
	public static Object parse(Element element, String name, Class<?> type, 
			Object parent, Object defValue){
		try{
			return parse(element, name, type, parent);
		}catch(Exception e){
			return defValue;
		}
	}
	
	public static Object parse(Node node, String name, Parseable parser){
		NodeList nodeList = node.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++){
			Node child = nodeList.item(i); 
			if (child.getNodeType() == Node.ELEMENT_NODE &&
				child.getNodeName().startsWith(name))
					parser.parse(child);
		}
		
		return parser;
	}
}

class DrawVisitor{
	int shiftY;
	int maxY;
	int freeSpaceHeight;
	
	/***
	 * Y позиция следующего печатаевомого элемента
	 */
	int top;
	/***
	 * Высота следующей строки
	 */
	int nextRowHeight;
	
	/***
	 * Высота страницы
	 */
	int pageHeight;
}

abstract class CellBase {
	protected Dimension size;
	protected Dimension location;
	private int fontSize;
	public static float FONT_SCALE = 4.05f;
	protected List<DataSource> source;
	
	
	protected CellBase(){
	}
	
	public static CellBase create(Context context, Node xmlNode, List<DataSource> source, int idx) 
		throws PropNotFound, BadPropFormat{
		CellBase result = null;
		
		Node node = xmlNode.getAttributes().getNamedItem("name");
		
		if (node != null){
			String cellName = node.getNodeValue();
		
			if(cellName.startsWith(TextCell.NAME))
				result = new TextCell();
			else if (cellName.startsWith(Table.NAME))
				result = new Table();
			else if (cellName.startsWith(LineCell.NAME))
				result = new LineCell();
			else if (cellName.startsWith(PictureCell.NAME))
				result = new PictureCell(context);
			else 
				return null;
		
			result.readProperties((Element)xmlNode);
			result.beforePrint(source, idx);
		}
		
		return result;
	}
	
	protected void readProperties(Element element) 
		throws PropNotFound, BadPropFormat{
		size = (Dimension) XmlElementParser
			.parse(element, "Size", Dimension.class, null);
		location = (Dimension) XmlElementParser
			.parse(element, "Location", Dimension.class, null);
		
		size.valX /= NPrinter.ZOOM_SCALE;
		size.valY /= NPrinter.ZOOM_SCALE;
		
		location.valX /= NPrinter.ZOOM_SCALE;
		location.valY /= NPrinter.ZOOM_SCALE;
	}
	
	public Rect getBounds(){
		Rect result = new Rect();
		
		result.left = location.valX;
		result.top = location.valY;
		result.right = result.left + size.valX;
		result.bottom = result.top + size.valY;
		
		return result;
	}
	
	public Dimension getSize(){
		return size;
	}
	
	public Dimension getLocation(){
		return location;
	}
	
	public abstract boolean draw(Canvas canvas, Paint paint, DrawVisitor visitor, int idx);
	
	public boolean drawPdf(com.itextpdf.text.Document doc, PdfWriter writer, DrawVisitor visitor, int idx) { return true; }
	
	public float getFontSize(){
		return FONT_SCALE * fontSize;
	}
	
	public void setFontSize(int size){
		fontSize = size / NPrinter.ZOOM_SCALE;
	}
	
	public void beforePrint(List<DataSource> source, int idx){
		this.source = source;
	}
	
	protected List<DataSource> getSource(){
		return source;
	}
}

class CellTextFormatter{
	protected final static char FIELD_SYM = '`';
	final static String EAN13 = "EAN13";
	final static String EAN8 = "EAN8";
	final static String CODE128 = "CODE128";
	final static byte[] pngHeader = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
	
	static boolean isPNG(byte[] src) {
		int i=0;
		for(; i<pngHeader.length && i < src.length; i++) {
			if(pngHeader[i] != src[i])
				return false;
		}
		
		return i == pngHeader.length;
	}
	
	public static Image getImage(String text, DataSource source, PdfWriter writer){
		Image ret = null;
		if(text.startsWith(NPrinter.IMAGE_MARKER)) {
			String[] parts = text.split(":");
			if(parts.length >= 2) {
				byte[] img = source.getImage(parts[1]);
				if(img != null)
					try {
						ret = Image.getInstance(img);
						int hgh = source.getImageHeight(parts[1]);
						if(hgh != 0) {
							int w = (int) ret.getWidth();
							int h = (int) ret.getHeight();
							if(h > hgh) {
								float coef = (float)hgh / h;
								ret.scaleAbsolute(w * coef, hgh);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		} else if(text.startsWith(NPrinter.BC_MARKER)) {
			String[] parts = text.split(":");
			if(parts.length == 3) {
				StringBuilder bc = new StringBuilder(), 
						bcType = new StringBuilder();
				if( source.getValue(bc, parts[1], null) && source.getValue(bcType, parts[2], null) && bc.length() > 0) {
					String bct = bcType.toString();
					Barcode bci = null;
					if(bct.equals(EAN13)) {
						bci = new BarcodeEAN();
						bci.setCodeType(Barcode.EAN13);
					} else if(bct.equals(EAN8)) {
						bci = new BarcodeEAN();
						bci.setCodeType(Barcode.EAN8);
					} else if(bct.equals(CODE128)) {
						bci = new Barcode128();
						bci.setCodeType(Barcode.CODE128);
					}
						
					if( bci != null ) {
						PdfContentByte cb = writer.getDirectContent();
						bci.setCode(bc.toString());
						ret = bci.createImageWithBarcode(cb, null, null);
						ret.scaleAbsoluteHeight(NPrinter.BARCODE_HEIGHT);
						if(ret != null)
							ret.setBorder(Image.NO_BORDER);
					}
				}
			}
		}
			
		return ret;
	}
	
	public static String format(String text, DataSource source){
		if(text.startsWith(NPrinter.IMAGE_MARKER) || text.startsWith(NPrinter.BC_MARKER))
			return "";
		
		int fldPos = text.indexOf(FIELD_SYM);
		
		if (fldPos == -1)
			return text;
		
		StringBuilder result = new StringBuilder();
		
		String str = text;
		
		while(str.length() > 0){
			if(fldPos == -1)
				result.append(str);
			else
				result.append(str.substring(0, fldPos));
			
			int fldPosNext = str.indexOf(FIELD_SYM, fldPos+1); 
			
			if (fldPosNext == -1){
				break;
			}
				
			String name = str.substring(fldPos+1, fldPosNext);
			String format = null;
			if( (fldPos = name.indexOf('|')) >= 0) {
				format = name.substring(fldPos+1);
				name = name.substring(0, fldPos);
			}
			
			StringBuilder value = new StringBuilder();
			
			if (source != null && source.getValue(value, name, format))
				result.append(value);

			str = str.substring(fldPosNext+1);
			fldPos = str.indexOf(FIELD_SYM);
		}
		
		return result.toString();
	}
}


interface Parseable{
	void parse(Node node);
}

class IterRepresentation{
	public static String asString(Iterable<?> rows){
		StringBuilder result = new StringBuilder();
		result.append('[');
		
		for (Object tc : rows) 
			result.append(tc.toString()).append(" ");

		result = result.deleteCharAt(result.length()-1);
		result.append(']');
		
		return result.toString();
	}
}

class TableRows extends Vector<TableRow>
	implements Parseable{
	private static final long serialVersionUID = 1L;
	private Table table;

	public TableRows(Table table){
		this.table = table;
	}
	
	@Override
	public void parse(Node node) {
		add((TableRow) XmlElementParser
				.parse(node, "Item", new TableRow(table)));
	}
	
	@Override
	public synchronized String toString() {
		return IterRepresentation.asString(this);
	}
	
	public Table getTable(){
		return table;
	}
}

class CellBorder{
	private static final int NONE = 0x0;
	private static final int TOP = 0x1;
	private static final int LEFT = 0x2;
	private static final int RIGHT = 0x4;
	private static final int BOTTOM = 0x10;
	
	private int value = TOP | LEFT | RIGHT | BOTTOM;
	
	public static CellBorder parseBorder(String text){
		CellBorder result = new CellBorder();
		result.value = NONE;
		
		if (text.contains("Top"))
			result.value |= TOP;
		if (text.contains("Left"))
			result.value |= LEFT;
		if (text.contains("Right"))
			result.value |= RIGHT;
		if (text.contains("Bottom"))
			result.value |= BOTTOM;
		
		return result;
	}
	
	public boolean isAll(){
		return value == (TOP | LEFT | RIGHT | BOTTOM);
	}
	
	public boolean isTop(){
		return (value & TOP) == TOP;
	}
	
	public boolean isBottom(){
		return (value & BOTTOM) == BOTTOM;
	}
	
	public boolean isLeft(){
		return (value & LEFT) == LEFT;
	}
	
	public boolean isRight(){
		return (value & RIGHT) == RIGHT;
	}
	
	public boolean isNone(){
		return value == NONE;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		if (isNone())
			result.append("None");
		else {
			if (isBottom())
				result.append("Bottom");
			if (isLeft())
				result.append("Left");
			if (isRight())
				result.append("Right");
			if (isTop())
				result.append("Top");
		}
				
		return super.toString();
	}

	public void setBorder(PdfPCell c) {
		int brd = PdfPCell.NO_BORDER;

		if( (value & LEFT) == LEFT ) brd |= PdfPCell.LEFT;
		if( (value & RIGHT) == RIGHT ) brd |= PdfPCell.RIGHT;
		if( (value & TOP) == TOP ) brd |= PdfPCell.TOP;
		if( (value & BOTTOM) == BOTTOM ) brd |= PdfPCell.BOTTOM;
		
		c.setBorder(brd);
	}
}

class VectorInteger extends Vector<Integer> 
	implements Parseable{
	
	private static final long serialVersionUID = 3715161571860272079L;

	@Override
	public synchronized String toString() {
		Iterator<Integer> iter = iterator();
		StringBuilder result = new StringBuilder();
		
		while(iter.hasNext())
			result.append(iter.next()).append(", ");
		
		result.delete(result.length() - 2, result.length());
		return result.toString();
	}

	@Override
	public void parse(Node node) {
		if(node != null && node.getFirstChild() != null &&
				node.getFirstChild().getNodeValue() != null)
			try{
				add(Integer.parseInt(node.getFirstChild().getNodeValue()) / NPrinter.ZOOM_SCALE);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
}

class Align{
	@SuppressWarnings("unused")
	private static final String TAG = "Align";
	
	/*HAlign*/
	private static final int TOP = 1;
	private static final int MIDDLE = 2;
	private static final int BOTTOM = 4;
	
	
	/*HAlign*/ 
	private static final int LEFT = 0x10;
	private static final int RIGHT = 0x20;
	private static final int CENTER = 0x40;
	
	
	private int value = MIDDLE | CENTER;
	
	public static Align parseAlign(String text){
		Align result = new Align();
		
		if (text.contains("Bottom"))
			result.value = BOTTOM;
		else if (text.contains("Top"))
			result.value = TOP;
		else
			result.value = MIDDLE;
		
		if (text.contains("Right"))
			result.value |= RIGHT;
		else if (text.contains("Left"))
			result.value |= LEFT;
		else
			result.value |= CENTER;
		
		return result;
	}

	public void setAlign(PdfPCell c) {
		int a = 0;
		if( (value & LEFT) == LEFT ) a = PdfPCell.ALIGN_LEFT;
		else if( (value & CENTER) == CENTER ) a = PdfPCell.ALIGN_CENTER;
		else if( (value & RIGHT) == RIGHT ) a = PdfPCell.ALIGN_RIGHT;
		c.setHorizontalAlignment(a);
		
		if( (value & TOP) == TOP ) a = PdfPCell.ALIGN_TOP;
		else if( (value & MIDDLE) == MIDDLE ) a = PdfPCell.ALIGN_MIDDLE;
		else if( (value & BOTTOM) == BOTTOM ) a = PdfPCell.ALIGN_BOTTOM;
		c.setVerticalAlignment(a);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		if ((value & BOTTOM) == BOTTOM)
			result.append("Bottom");
		else if ((value & TOP) == TOP)
			result.append("Top");
		else  
			result.append("Middle");
		
		if ((value & RIGHT) == RIGHT)
			result.append("Right");
		else if ((value & LEFT) == LEFT)
			result.append("Left");
		else
			result.append("Center");
		
		return result.toString();
	}
	
	public boolean isMiddle(){
		return (value & MIDDLE) == MIDDLE;
	}
	
	public boolean isTop(){
		return (value & TOP) == TOP;
	}
	
	public boolean isBottom(){
		return (value & BOTTOM) == BOTTOM;
	}
	
	public boolean isLeft(){
		return (value & LEFT) == LEFT;
	}
	
	public boolean isCenter(){
		return (value & CENTER) == CENTER;
	}
	
	public boolean isRight(){
		return (value & RIGHT) == RIGHT;
	}
}

class Dimension{
	public int valX;
	public int valY;
	
	public static Dimension parseDimension(String text){
		Dimension result = new Dimension();
		String strVal1 = text.substring(0, text.indexOf(","));
		String strVal2 = text.substring(text.indexOf(",") + 1);
		
		result.valX = Integer.parseInt(strVal1.trim());
		result.valY = Integer.parseInt(strVal2.trim());
		
		return result;
	}
	
	private Dimension(){}
	
	public Dimension(int x, int y){
		valX = x;
		valY = y;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s", Integer.toString(valX), Integer.toString(valY));
	}
}

class Painter{
	public static void paintText(Canvas canvas, Paint paint, 
			String text, int height, Rect bounds, Dimension offset,
			Align align) {
		Rect rectBounds = new Rect();
		
		rectBounds.top = bounds.top + offset.valY;
		rectBounds.bottom = bounds.bottom - offset.valY;
		rectBounds.left = bounds.left + offset.valX;
		rectBounds.right = bounds.right - offset.valX;
		
		Dimension textPos = new Dimension(rectBounds.left, rectBounds.top);
		
		if(align.isMiddle())
			textPos.valY = rectBounds.top + 
				(rectBounds.bottom - rectBounds.top) / 2 - height / 2;
		else if (align.isBottom())
			textPos.valY = rectBounds.bottom - height;
		
		Alignment alignment;
		
		if (align.isRight())
			alignment = Alignment.ALIGN_OPPOSITE;
		else if (align.isCenter())
			alignment = Alignment.ALIGN_CENTER;
		else
			alignment = Alignment.ALIGN_NORMAL;
		
		canvas.save();
		canvas.translate(textPos.valX, textPos.valY);
		
		TextPaint tp = new TextPaint(paint);
		Typeface tf = Typeface.create("Arial", Typeface.NORMAL);
		tp.setTypeface(tf);
		
		canvas.clipRect(new Rect(0, 0, 
				rectBounds.right - rectBounds.left, rectBounds.bottom - rectBounds.top));
		
		StaticLayout layout = new StaticLayout(text, tp, 
				rectBounds.right - rectBounds.left, 
				alignment, 1.0f, 0.0f, true);
		layout.draw(canvas);
		
		canvas.restore();
	}
}
