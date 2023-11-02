package com.grsoft.napoleon.modules.print;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Util;

public class TextPrinter extends GraphicPrinter{
	public static final String FILE_ENCODE = "cp1251";
	public static String OUTPUT_FILE_ENCODE = "cp1251";
	public static final String EOL = "\r\n";
	public static final char NEXT_PAGE = 0xC;
	
	public static int PAGE_ROW_COUNT = 102; //142;
	public static String STR_DIVIDER = " .,-+%*\\";
	public static String STR_DIVIDER_BEFORE = "\"\'<№#";
	
	static int FILE_COUNT = 0;
	static long CUR_DATE = 0; // когда переходим на друго день, сбрасываем ссчетчик файлов

	public interface FormLoader {
		String getForm(String repName);
	}
	
	static public FormLoader FormLoader = new StdFormLoader();
	
	
	public TextPrinter(){
	}
	
	@Override
	protected String wrapResourceName(String name) {
		return name + "t";
	}
	
	public boolean rowHasCell(String row){
		int posLeft = row.indexOf('`');;
		int pos = row.indexOf('`', posLeft + 1);
		
		return posLeft != -1 && pos != -1;
	}
	
	public static int readStream(Reader reader, char guard, StringBuilder output){
		int result = -1;
		StringBuilder data = new StringBuilder();
		try{
			do{
				result = reader.read();
				data.append((char)result);
			}while(result != -1 && result != guard);
			
			if (output != null)
				output.append(data.toString()
						.substring(0, data.length()-1));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static TPCell readCell(Reader is, DataSource source){
		TPCell result = null;
		StringBuilder dataB = new StringBuilder();

		try{
			if (readStream(is, '`', dataB) != -1){
				String data = dataB.toString();;
				ObjAttr oa = new ObjAttr(data.length()); 
				StringBuilder value = new StringBuilder();
				
				StringBuilder unformatCellValue = new StringBuilder();
				while (readObject(data, oa, value, source))
					unformatCellValue.append(value);
				
				// уберем пустые пробелы сзади
				unformatCellValue.append(value.toString().replaceAll("\\s+$",""));
				
				String appStr = unformatCellValue.toString();
				
				result = new TPCell(oa, appStr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean readObject(String data, ObjAttr ca, 
			StringBuilder value, DataSource source){
		boolean result = false;
		
		value.setLength(0);
		int l = data.indexOf("[", ca.pos);
		
		if (l != -1){
			if(ca.pos != l && ca.pos < l){
				value.append(data.substring(ca.pos + 1, l));
				ca.pos = l;
				result = true;
			}else{
				l++;
				ca.pos = data.indexOf("]", l);
				String objectName = data.substring(l, ca.pos);
				
				if (objectName.length() > 0){
					if (objectName.charAt(0) == '#'){
						String[] codes = objectName.substring(1,objectName.length()).split(",");
						int arrLen = codes.length;
						if(arrLen > 0)
							ca.width = Integer.parseInt(codes[0]);
						if (arrLen > 1)
							ca.align = codes[1].length() > 0 ? codes[1] : "l"; 
					} else {
						String format = null;
						int div = objectName.indexOf(':');
						if( div != -1 ) {
							format = objectName.substring(div+1);
							objectName = objectName.substring(0, div);
						}
						source.getValue(value, objectName, format);
					}
					
					result = true;
				}
			}
		} else if( ca.pos < data.length() && data.length() > 0 ) {
			value.append(data.substring(ca.pos + 1));
		}
		
		return result;
	}
	
	@Override
	public File print(Context context, String repName, DataSource source) {
		List<DataSource> srca = new ArrayList<DataSource>();
		srca.add(source);
		return print(context, repName, srca);
	}
	
	@Override
	public File print(Context context, String repName, List<DataSource> source) {
		File result = null;
		
		if(NPrinter.forms.containsKey(repName) && source.size() > 0){
			String form = getForm(context, repName);
			StringBuilder output = makeContent(context, source.get(0), form);
			result = savePrintResult(context, output);
		}
		
		return result;
	}

	protected StringBuilder makeContent(Context context, DataSource source, String form) {
		//Debug.startMethodTracing("makeContent"); 
		
		StringBuilder output = new StringBuilder();
		StringReader stream = new StringReader(form);
		BufferedReader reader = new BufferedReader(stream);
		
		source.startPage();
		int lineIndex = 0;
		try{
			String line = null;
			do{
				line = reader.readLine();
				if (line == null)
					break;
				
				String ts = line.trim(); 
				if (ts.equals("`table begin`")){
					TPTable table = new TPTable(reader, source, lineIndex++,  BTPrinterHelper.getSettings(context).row_count);
					output.append(table.getValue());
					lineIndex = table.getPos();
				}else if (ts.equals("`table end`")){
					// nothing to do
				} else {
					lineIndex = parseLine(source, output, line, lineIndex);
//					boolean haveData = false;
//					ArrayList<TPCell> cells = new ArrayList<TPCell>();
//					
//					do{
//						BufferedReader lineReader = new BufferedReader(new StringReader(line));
//						haveData = parseLineCells(source, output, cells, lineReader);
//						
//						if(haveData)
//							output.append(EOL);
//						lineIndex++;
//					}while(haveData);
//						
//					output.append(EOL);	
				}
			}while(line != null);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		output.append(NEXT_PAGE);
		//Debug.stopMethodTracing();
		
		return output;
	}
	
	public static int parseLine(DataSource source, StringBuilder output, String line, int lineIndex) {
		boolean haveData = false;
		ArrayList<TPCell> cells = new ArrayList<TPCell>();
		
		do{
			BufferedReader lineReader = new BufferedReader(new StringReader(line));
			haveData = parseLineCells(source, output, cells, lineReader);
			
			if(haveData)
				output.append(EOL);
			lineIndex++;
		}while(haveData);
			
		output.append(EOL);	
		return lineIndex;
	}

	public static boolean parseLineCells(DataSource source, StringBuilder output, ArrayList<TPCell> cells, BufferedReader lineReader) {
		boolean haveData;
		haveData = false;
		int c = -1;
		int cellInd = 0;
		
		do{
			c = readStream(lineReader, '`', output);
			
			if (c == '`'){
				TPCell cell = null;
				
				if (cells.size() > cellInd){
					cell = cells.get(cellInd);
					c = readStream(lineReader, '`', null);
				} else {
					cell = readCell(lineReader, source);
					cells.add(cell);
				}
				
				output.append(cell.getValue());
				
				if(!haveData)
					haveData = cell.haveMoreData();
				
				cellInd++;
			}
		}while(c != -1);
		return haveData;
	}

	protected File savePrintResult(Context context, StringBuilder output) {
//		File cacheDir = new File(Environment.getExternalStorageDirectory(), 
//				"Android/data/" + context.getPackageName() +"/files/");
//		if(!cacheDir.exists())
//			cacheDir.mkdirs();

		long curdate = Util.getDate().getTime();
		if( curdate > CUR_DATE ) {
			CUR_DATE = curdate;
			FILE_COUNT = 1;
		}
		
		File cacheDir = new File(Path.getFilesDir());
		File result = new File(cacheDir, String.format("output%d.txt", FILE_COUNT++));
		
		OutputStreamWriter osw = null;
		try{
			osw = new OutputStreamWriter(new FileOutputStream(result), OUTPUT_FILE_ENCODE);
			osw.write(output.toString());
			osw.flush();
			osw.close();
			osw = null;
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if( osw != null )
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	protected String getForm(Context context, String repName) {
		String form = "";
		if( FormLoader != null )
			form = FormLoader.getForm(getResourceName(repName));
		
		if(form == null || form.length() == 0)
			form = readString(context, repName);

		return form;
	}

	private String readString(Context context, String repName){
		String result = "";
		try{
		    StringBuilder sb = new StringBuilder();
			List<Integer> forms = getFormidByName(repName, context);
			if( forms.size() > 0 ) {
				InputStream input = context.getResources().openRawResource(forms.get(0));
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, FILE_ENCODE));
			    String line = null;
			    while ((line = reader.readLine()) != null) {
			      sb.append(line + "\n");
			    }
			    input.close();
			}
		    result = sb.toString();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void sendPrintTask(Context context, File file) {
		Intent intent = new Intent(NPrinter.SEND_TXT_FILE_ACTION);
		intent.putExtra("file", file.getAbsolutePath());
		
		context.sendBroadcast(intent);
	}
}

class RowAttr{
	public int posLeft = 0;
	public int pos = 0;
	public boolean next = false;
}

class ObjAttr extends RowAttr{
	public String align = "l";
	public int width = 0; 

	public ObjAttr(int width){
		this.width = width;
		this.pos = -1; // для корректной работы readObject
	}
}

class TPCell {
	public int width;
	public String value;
	public int pos = 0;
	public String align = "l";
	
	public TPCell(){
		
	}
	
	public TPCell(ObjAttr oa, String value){
		this.width = oa.width;
		this.align = oa.align;
		this.value = value;
	}

	public String getValue() {
		String result = "";
		final int LAST_POS = 25;
		
		int rem = (value.length() - pos); 
		if (rem > 0 ){
			int right = pos + width;
			boolean canDivide = false;
			if (right >= value.length())
				right = value.length();
			else
				canDivide = true;

			while( pos < right && value.charAt(pos) == ' ' )
				pos++;
			String curStr = value.substring(pos, right);
			if( canDivide ) {
				int i=1;
				for( ; i<=LAST_POS && i < curStr.length(); i++ ) {
					char sym = curStr.charAt(curStr.length() - i);
					String symVal = String.valueOf(sym);
					if( TextPrinter.STR_DIVIDER.contains(symVal)) {
						i--;
						break;
					}
					if( TextPrinter.STR_DIVIDER_BEFORE.contains(symVal)) {
						break;
					}
				}
				if( i < LAST_POS && i < curStr.length() ) {
					curStr = curStr.substring(0, curStr.length() - i);
				}
			}
			result = alignString(curStr);			
			pos += curStr.length();
		}else{
			char[] dummy = new char[width];
			Arrays.fill(dummy, ' ');
			result = new String(dummy);
		}
		
		return result;
	}
	
	public boolean haveMoreData(){
		return pos < value.length();
	}
	
	private String alignString(String str){
		String result = String.format("%-"+width+"s", str);
		
		if (align.equals("r"))
			result = String.format("%"+width+"s", str);
		else if (align.equals("m") && width > str.length()){
			int padCount = width - str.length();
			int leftPad = padCount / 2;
			int rightPad = padCount - leftPad;
			
			char[] left = new char[leftPad];
			char[] right = new char[rightPad];
			Arrays.fill(left, ' ');
			Arrays.fill(right, ' ');
			
			StringBuilder sb = new StringBuilder();
			sb.append(left);
			sb.append(str);
			sb.append(right);
			
			result = sb.toString();
		}
		
		return result;
	}
	
	public int getHeigth(){
		int result = 0;
		BufferedReader reader = new BufferedReader(new StringReader(value));
		
		try{
			String line = reader.readLine();
			while(line != null){
				result++;
				line = reader.readLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}

class TPTable extends TPCell{
	
//	private String readTill(BufferedReader reader, String anchor){
//		StringBuilder result = new StringBuilder();
//		try{
//			String curr = "";
//			do{
//				curr = reader.readLine();
//				
//				if (curr != null && !curr.trim().equals(anchor))
//					result.append(curr).append(TextPrinter.EOL);
//			}while(curr != null && !curr.equals(anchor));	
//			
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return result.toString();
//	}
	
	class Head extends TPCell{
		final static String HEAD_END = "`head end`";
		
		public Head(BufferedReader reader, DataSource source) {
			StringBuilder result = new StringBuilder();
			try{
				do{
					String curr = reader.readLine();
					
					if (curr == null )
						break;
					if( curr.trim().equals(HEAD_END)) {
//						result.append(TextPrinter.EOL);
						break;
					}
					
					TextPrinter.parseLine(source, result, curr, 0);
				}while(true);	
			}catch(Exception e){
				e.printStackTrace();
			}
			value = result.toString();
//
//			value = readTill(reader, HEAD_END);
		}
		
		@Override
		public String getValue() {
			return value;
		}
	}
	
	class RowFooter extends Row{

		public RowFooter(String attr, BufferedReader reader, DataSource source) {
			super(attr, reader, source);
		}
		
	}
	
	class Row extends TPCell{
		DataSource source;
		final static String ROW_END = "`row end`";
		public final static String NAME = "`row`";
		private final static int DEF_ROW_HEIGHT = 2;
		
		public int height = DEF_ROW_HEIGHT;
		public boolean decor = true;
		
		int lines = 0; 
		
		protected String border;
		private boolean haveMoreData = true;
		
		 
		protected String name = NAME;
		
		public Row(String attr, BufferedReader reader, DataSource source) {
			init(attr, reader);
			this.source = source;
		}
		
		protected void init(String attr, BufferedReader reader) {
			try{
				initAttr(attr);
				value = readValue(reader);
				
				if(decor)
					border = reader.readLine();
				
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		protected String readValue(BufferedReader reader) throws IOException {
			StringBuilder sb = new StringBuilder();
			int h = height - (decor ? 1 : 0);
			
			for(int i = 0; i < h; i++){
				sb.append(reader.readLine());
				
				if(i + 1 < h)
					sb.append(TextPrinter.EOL);
			}
			
			return sb.toString();
		}

		protected void initAttr(String attr) {
			attr = attr.substring(attr.indexOf(name) + name.length()).trim();
			
			String[] arr = attr.split(" ");
			
			for(int i = 0; i < arr.length; i++){
				String[] val = arr[i].split("=");
				if(val[0].length() == 0 )
					continue;
				try{
					Field f = getClass().getField(val[0]);
					
					if(f.getType() == int.class)
						f.set(this, Integer.parseInt(val[1]));
					else
						f.set(this, val[1]);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		@Override
		public String getValue() {
			StringBuilder result = new StringBuilder();
			boolean haveData = false;
			ArrayList<TPCell> cells = new ArrayList<TPCell>();
			do{
				BufferedReader lineReader = new BufferedReader(new StringReader(value));
				int c = -1;
				int cellInd = 0;
				haveData = false;
				
				do{
					c = TextPrinter.readStream(lineReader, '`', result);
					
					if (c == '`'){
						TPCell cell = null;
						
						if (cells.size() > cellInd){
							cell = cells.get(cellInd);
							c = TextPrinter.readStream(lineReader, '`', null);
						} else {
							cell = TextPrinter.readCell(lineReader, source);
							cells.add(cell);
						}
						
						result.append(cell.getValue());
						
						if(!haveData)
							haveData = cell.haveMoreData();
						
						cellInd++;
					}
					
				}while(c != -1);
				
//				if (haveData)
				result.append(TextPrinter.EOL);
			}while(haveData);
			
//			result.append(TextPrinter.EOL);
			if( border != null ) {
				lines++;
				result.append(border).append(TextPrinter.EOL);
			}
			source.calculate();
			haveMoreData = source.moveNext();
			
			lines = 0;
			int start = 0;
			while((start = result.indexOf(TextPrinter.EOL, start) + 1) > 0 )
				lines++;
			
			return result.toString();
		}
		
		/**
		 * Число напечатанный строк в функции getValue()
		 * @return
		 */
		public int getLines() { return lines; }
		
		
		@Override
		public int getHeigth() {
			return height;
		}
		
		public boolean haveMoreData(){
			return haveMoreData;
		}

		public void startPage() {
			source.startPage();
		}
	}
	
	class Footer extends Row{
		public Footer(String attr, BufferedReader reader, DataSource source) {
			super(attr, reader, source);
		}
	}
	
	class Group extends Row{
		private String group;
		private String grborder;
		private String row;
		private String rborder;
		public static final String NAME = "`group`";
		private int rowHeight;
		
		public Group(String attr, BufferedReader reader, DataSource source) {
			super(attr, reader, source);
		}
		
		@Override
		protected void init(String attr, BufferedReader reader) {
			try{
				name = NAME;
				initAttr(attr);
				
				group = readValue(reader);
				
				if(decor)
					grborder = reader.readLine();
				
				String lint = reader.readLine();
				
				Row row = new Row(lint, reader, source);
				this.row = row.value;
				this.rborder = row.border;
				this.rowHeight = row.getHeigth();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		public String getValue() {
			DataSourceGroup sourceGroup = (DataSourceGroup) source;
			
			if (sourceGroup.isGroup()){
				value = group;
				border = grborder;
			}else{
				value = row;
				border = rborder;
			}
				
			return super.getValue();
		}
		
		@Override
		public int getHeigth() {
			DataSourceGroup sourceGroup = (DataSourceGroup) source;
			
			if (sourceGroup.isGroup())
				return height;
			else 
				return rowHeight;
		}
	}
	
	Head head;
	Row row;
	Footer footer;
	RowFooter rowFooter;
	
	DataSource source;
	int pos; 
	int pageHeight;
	int footerHeight;
//	int leftHeight;
	
	
	public TPTable(BufferedReader reader, DataSource source, int pos, int pageHeight) {
		this.source = source;
		this.pos = pos;
		this.pageHeight = pageHeight;
		
//		int height = 0;
		
		try{
			String varName = reader.readLine();
			String line = reader.readLine(); 
			
			if (line.trim().equals("`head begin`")){
				head = new Head(reader, source);
				line = reader.readLine();
//				height += head.getHeigth();
			}
			
			if (line != null && line.contains("`group`")){
				row = new Group(line, reader, source.getObject(varName));
				line = reader.readLine();
//				height += row.getHeigth();
			}else if (line != null && line.contains("`row`")){
				row = new Row(line, reader, source.getObject(varName));
				line = reader.readLine();
//				height += row.getHeigth();
			}
				
			footerHeight = 0;

			if (line != null && line.contains("`total`")){
				rowFooter = new RowFooter(line, reader, source);
				line = reader.readLine();

				footerHeight = rowFooter.getHeigth();
//				height += footerHeight;
			}
			    			
			if (line.contains("`footer`")){
				footer = new Footer(line, reader, source);
				int fh = footer.getHeigth();
				if( footerHeight < fh )
					footerHeight = fh;
			}
			
//			leftHeight = pageHeight - (pos + height + footHeight);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getPos() { return pos; }

	@Override
	public String getValue() {
		StringBuilder result = new StringBuilder();
		
		int lastPos = pageHeight - footerHeight;
		String rowData = null;
		int rowDataLines = 0;
		while(true){
			result.append(head.getValue());
			pos += head.getHeigth();
			
			if( rowData != null ) {
				result.append(rowData);
				pos += rowDataLines;
				rowData = null;
			}
			
			while(pos < lastPos && row.haveMoreData()) {
				rowData = row.getValue();
				rowDataLines = row.getLines();
				
				if( pos + rowDataLines > lastPos )
					break;
				
				result.append(rowData);
				pos += rowDataLines;
				rowData = null;
			}
			
			if (rowFooter != null) {
				String val = rowFooter.getValue();
				result.append(val);
				pos += rowFooter.getLines();
			}
						
			if (rowData == null && !row.haveMoreData()) {
				if( footer != null ) {
					String val = footer.getValue();
					if( pos + footer.getLines() > pageHeight ) {
						result.append(TextPrinter.NEXT_PAGE);
						pos = 1;
					}
					result.append(val);
					pos += footer.getLines();
					
				}
				break;
			}
			
			result.append(TextPrinter.NEXT_PAGE);
			pos = 1;
			row.startPage();
			SystemClock.sleep(2000);
		}
		
		return result.toString();
	}
	
//	private boolean canPrintOnPage(int pos){
//		return pos < leftHeight;
//	}
}