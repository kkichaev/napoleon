package com.grsoft.napoleon;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.WSales;
import com.grsoft.dataobjects.WSalesItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.dataobjects.impl.WSalesImpl;
import com.grsoft.napoleon.modules.print.BaseDataSource;
import com.grsoft.napoleon.modules.print.GraphicPrinter;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.napoleon.printsources.SalesSourceEx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class SalesDetailEx extends SalesDetail implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setVisibility(View.VISIBLE);
		
		findViewById(R.id.btnWSales).setOnClickListener(this);
	}
	
	@Override
	protected String[] createPrintCaption() {
//		SalesEx se = (SalesEx)doc.getData();
		boolean isBlack = false; //(se.isBlack != 0 || se.isExchange !=0 || se.isExpired != 0);
		return isBlack ? new String[] { "Накладная", "Доверенность" } : 
				new String[] {
						NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION, "Счет", 
						NPrinter.UPD_CAPTION, "ТТН", 
						"Доверенность", 
						"Удостоверение качества"};
	}
	
	protected SelectPrinFormDlg createPrintDlg() {
		return new SPFDialog(this, WAIT_FOR_PRINT_DLG);
	}
	
	@Override
	public void onBackPressed() {
		if( doc instanceof SalesImplEx)
			((SalesImplEx)doc).refreshDocSum();
		super.onBackPressed();
	}

	@Override
	public void doSend() {
		if(canPrint()) {
			super.doSend();
		}
	}

	boolean canPrint() {
		OrgImpl oi = new OrgImpl();
		oi.read("id", doc.getId());
		if(((OrgEx)oi.getData()).checkMark != 0) {
			if(!((SalesImplEx)doc).isScanned()) {
				Toast.makeText(this, "Необходимо просканировать весь товар", Toast.LENGTH_LONG).show();
				SalesScan.openScan(this, (SalesImplEx) doc);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void doPrint() {
		if(!canPrint())
			return;

		try {
			Dialog d = selectPrintFormDlg.createDialog(createPrintCaption());
			SalesPrint sp = SalesPrintType.getConstructor(Sales.class).newInstance((Sales)doc.getData());
			selectPrintFormDlg.setDataSource(new SalesSourceEx(sp));
			d.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class SPFDialog extends SelectPrintFormDlgNew {

		public SPFDialog(Context context, int waitDlgid) {
			super(context, waitDlgid);
		}
		
		public void setInPack(boolean inPack) {
			SalesPrint sp = ((SalesSource)dataSource).getData();
			if(sp instanceof SalesPrintEx)
				((SalesPrintEx)sp).setInPack(inPack);
		}
		
		@Override
		protected void doPrint(DialogInterface dialog) {
			CheckBox cb = (CheckBox)((Dialog)dialog).findViewById(R.id.cbInPice);
			setInPack(!cb.isChecked());
			
			super.doPrint(dialog);
		}
		
		@Override
		protected void addFormToPrint(StringBuilder sb, int count, String formCaption) {
			if(formCaption.compareTo("ТТН") == 0 && count > 0) {
				formCaption = "ТТН" + GraphicPrinter.FORM_DELIM_SIM + "ttn_back";
			}
			
			super.addFormToPrint(sb, count, formCaption);
		}
		
		@Override
		protected void beforePrint(StringBuilder sb) {
			sb.append(GraphicPrinter.FORM_DELIM_SIM + "party_nakl");
		}
		
		@Override
		public Dialog createDialog(String[] captions) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			if( title != null )
				builder.setTitle(title);
			else
				builder.setTitle(R.string.print_docs_copies);
			
			View v = View.inflate(context, R.layout.select_print_form, null);
			ListView list = (ListView)v.findViewById(R.id.list);
//			list.setId(R.id.list);
			list.setAdapter(new Adapter(captions));
			builder.setView(v);
			
			builder.setPositiveButton(R.string.ok, okclick);
			builder.setNegativeButton(R.string.cancel, null);
			
			return builder.create();
		}
	}
	
	protected void receiveWSales() {
		new RcvWSales(this, ((SalesEx)doc.getData()).barcode) {
			protected void onPostExecute(Boolean result) {
				boolean noDoc = true;
				if (result) {
					WSalesImpl impl = new WSalesImpl();
					
					if (impl.read("barcode", ((SalesEx)doc.getData()).barcode)) {
						printWSale(impl.getData(), (Sales) doc.getData());
						noDoc = false;
					}
				}
				if(noDoc){
					Toast.makeText(getApplicationContext(), "Документ еще не сформирован", Toast.LENGTH_SHORT).show();
				}
			}; 
		}.execute();
	}
	
	private void printWSale(WSales wSales, Sales sales) {
		final BaseDataSource ods = new BaseDataSource(new WSalesPrintData(wSales, sales));
		NPrinter.IMAGE_PADDING = 25;
		SelectPrinFormDlg.createPrintForm(SalesDetailEx.this, ods, WAIT_FOR_PRINT_DLG, "wsales", null);
	}

	
	public static class WSalesPrintData {

		public List<WSalesItemDS> items = new ArrayList<WSalesItemDS>();

		public WSalesPrintData(WSales data, Sales sales) {
			for(WSalesItem i : data.items)
				items.add(new WSalesItemDS(new WSalesPrintDataItem(i, sales, data)));
		}
	}
	
	public static class WSalesItemDS extends BaseDataSource {
		public final static int WHITE = 0xFFFFFFFF;
		public final static int BLACK = 0xFF000000;
		
		public final static int WIDTH = 100;
		public final static int HEIGHT = 100;
		
		final String PREF = "http://mercury.vetrf.ru/pub/operatorui?_language=ru&_action=showVetDocumentFormByUuid&uuid=";

		public WSalesItemDS(Object object) {
			super(object);
		}
		
		@Override
		public byte[] getImage(String name) {
			byte[] ret = null;
			String code = ((WSalesPrintDataItem)object).code; 
			
			Bitmap bmp = encodeAsBitmap(String.format("%s%s", PREF, code));
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			ret = stream.toByteArray();
			bmp.recycle();
			
			return ret;
		}
		
		Bitmap encodeAsBitmap(String str) {
		    BitMatrix result;
		    try {
		    	
		        result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
		    int width = result.getWidth();
		    int h = result.getHeight();
		    int[] pixels = new int[width * h];
		    for (int y = 0; y < h; y++) {
		        int offset = y * width;
		        for (int x = 0; x < width; x++) {
		            pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
		        }
		    }
		    Bitmap bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_8888);
		    bitmap.setPixels(pixels, 0, width, 0, 0, width, h);
		    return bitmap;
		}
	}
	
	public static class WSalesPrintDataItem {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		
		public String info = "";
		public String id = "";
		public String code = "";
		
		
		public WSalesPrintDataItem(WSalesItem i, Sales sales, WSales wsales) {
			code = i.code;
			
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("<b>Форма 2 ВСД от %s МСК (Оформлен)</b><br/>", sdf.format(sales.created)));
			sb.append(String.format("Фирма-отправитель: %s<br/>", wsales.firm_sender));
			sb.append(String.format("Фирма-получатель: %s<br/>", wsales.firm_recipient));
			sb.append(String.format("Предприятие-получатель: %s<br/>", wsales.org_recipient));
			sb.append(String.format("Продукт: %s<br/>", i.product));
			sb.append(String.format("Выработана: %s<br/>", i.info));
			sb.append(String.format("Код: <b>%s</b><br/>", i.code));
			
			info = sb.toString();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnWSales) {
			WSalesImpl impl = new WSalesImpl();
			
			if (impl.read("barcode", ((SalesEx)doc.getData()).barcode))
				printWSale(impl.getData(), (Sales) doc.getData());
			else
				receiveWSales();
		}
		
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetailex);
	}
}
