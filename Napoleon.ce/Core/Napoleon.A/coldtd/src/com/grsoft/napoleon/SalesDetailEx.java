package com.grsoft.napoleon;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesToPrint;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.StdFormLoader;
import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.napoleon.utl.PackShowHelper;
import com.grsoft.napoleon.utl.ScannerHelper;
import com.grsoft.napoleon.utl.ServerFormReader;
import com.grsoft.napoleon.utl.WiFiPrint;
import com.grsoft.napoleon.utl.WiFiPrinterConfig;

public class SalesDetailEx extends SalesDetail {

	String fileName;
	AtomicBoolean isScanning=new AtomicBoolean(false);
	
	
	@Override
	protected void doPrint() {
		if( ((Sales)doc.getData()).supplyercode.length() == 0 ) {
			Toast.makeText(this, "В документе не выбран поставщик. Пожалуйста, войдите в параметры продажи и нажмите OK", Toast.LENGTH_SHORT).show();
			return;
		}
		super.doPrint();
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
		
		findViewById(R.id.btnPrintOnLine).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { printOnLine(); }
		});
	
		findViewById(R.id.btnPrint).setOnClickListener(new OnClickListener() {			
			@Override public void onClick(final View v) { printOffLine(); }
		});
	}
	
	protected void printOffLine() {
		selectPrintFormDlg.setTitle(getString(R.string.print_docs));
		TextPrinter.FormLoader = new StdFormLoader();
		doPrint();
	}

	protected void printOnLine() {
		TextPrinter.FormLoader = new TextPrinter.FormLoader() {
			
			@Override
			public String getForm(String repName) {
				StringBuilder sb = new StringBuilder();
				try {
					Object o = new Object();
					SalesToPrint sp = new SalesToPrint();
					DataObject.makeCopy(sp, doc.getData());
					sp.formName = repName;
					ServerFormReader.getForm(SalesDetailEx.this, sp, o, sb);
					synchronized (o) {
						o.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if( sb.length() == 0 ) {
					runOnUiThread(new Runnable() {
						@Override public void run() { Toast.makeText(getApplicationContext(), "Не могу получить форму", Toast.LENGTH_SHORT).show(); }
					});
					throw new RuntimeException("Test");
				}
				return sb.toString();
			}
		};
		selectPrintFormDlg.setTitle("Запрос внешней формы");
		doPrint();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
		    
	        if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)){
				fileName  = intent.getStringExtra("file");
				printing();						
			}
		}
	};

	@Override
	public void onBackPressed() {
		TextPrinter.FormLoader = new StdFormLoader();
		
		super.onBackPressed();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		isScanning.set(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 132:
			NapoleonEx.moveTo(this);
			break;
		case 212:
		case 221:
			if( isScanning.compareAndSet(false, true) ) {
				scan();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void updateTotalSum() {

		SalesImplEx sales = (SalesImplEx)doc;
		TextView tvTotalSum = (TextView)findViewById(R.id.tvTotalSum);

		int count = sales.count(), countPack = sales.countPack();
		long sum = sales.sum();
		
		PackShowHelper.updateTotalSum(tvTotalSum, sum, count, countPack);
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		if( item.inPack() ) {
			PackShowHelper.drawItemQty(color, item, tvQty, (PriceEx) price.getData());
		} else
			super.drawItemQty(color, item, tvQty);
	}
	
	void scan() {
		Thread scanThread = new Thread() {
			public void run() {
				ScannerHelper.doScan(SalesDetailEx.this, doc);
				isScanning.set(false);
			}
		};
		scanThread.start();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	};
	
	protected void printing() {
		if( fileName != null ) {
			Toast.makeText(this, "Документ отправлен на печать", Toast.LENGTH_SHORT).show();
			WiFiPrinterConfig cfg = WiFiPrinterConfig.get(this);
			WiFiPrint.print(cfg, SalesDetailEx.this, fileName);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
}
