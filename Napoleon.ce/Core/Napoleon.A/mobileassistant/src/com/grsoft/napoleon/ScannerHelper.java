package com.grsoft.napoleon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Consts;

public class ScannerHelper {
	private static final String TAG = "ScannerHelper";
	
	OrderImpl doc;
	PriceImpl price= new PriceImpl();
	StringBuilder barCode = new StringBuilder();
	CostStrategy cs;
	DocUpdated handler;
	
	BroadcastReceiver onscanrcv = new BroadcastReceiver() {
			
		@Override
		public void onReceive(Context context, Intent intent) {
			String barCode = intent.getStringExtra("Scan_context");
			doBarcode(barCode);
		}
	};
	
	
	public void registerReciver(Activity a){
		a.registerReceiver(onscanrcv, new IntentFilter("com.android.scanservice.scancontext"));
	}
	
	public void unregisterReciver(Activity a){
		a.unregisterReceiver(onscanrcv);
	}
	
	@SuppressLint("SimpleDateFormat")
	void putToLog(String text) {
		if( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
			return;
		
		String LOG_NAME = "scanner.txt";
		File dir = new File(Environment.getExternalStorageDirectory() + "/" + Path.SHARED_FOLDER);
		if( !dir.exists() )
			dir.mkdir();
		
		File f = new File(dir, LOG_NAME);
		
		try {
			PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
			wr.println(sdf.format(new Date()) + " " + text);
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public interface DocUpdated {
		void updated(OrderImpl doc, PriceImpl p);
	}
	
	public ScannerHelper(OrderImpl doc, DocUpdated handler) {
		this.doc = doc;
		cs = CostStrategy.getInstance(doc.getClass());
		this.handler = handler;
		
		putToLog("Creating");
	}
	
	public void close() {
		price.close();
	}
	
	@SuppressLint("DefaultLocale")
	public boolean onKeyDown(KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_UP)
			return true;
		
		int code = event.getKeyCode();
		
//		if( code == 140 ) Mogic code: what do it?
//			return false;
		
		String text = String.format("Got %d,  %c", code, event.getNumber());
		putToLog(text);
		Log.d(TAG, text);
		
		if(code == KeyEvent.KEYCODE_ENTER || event.getUnicodeChar() == '\n') {
			if( barCode.length() > 0) {
				String tval = barCode.toString();
				barCode.delete(0, barCode.length());
				doBarcode(tval);
			}
		} else
			barCode.append(event.getNumber());
		
		return true;
	}

	private void doBarcode(String tval) {
		Log.d(TAG, "doBarcode: " + tval);
		String table = price.getTableName();
		String where = "barcode LIKE '%|" + tval + "|%'";
		putToLog("Search " + where);
		
		List<Long> ids = DbReader.readIds(table, where, null);
		if( ids.size() > 0 ) {
			price.read(ids.get(0));
			Price p = price.getData();
			OrderItem oi = (OrderItem) doc.findItem(p.id);
			int cost = cs.getItemCost(p, doc);
			doc.updateQty(price, (oi == null) ? Consts.QTY_SCALE : oi.qty + Consts.QTY_SCALE, cost, false);
			if( handler != null && doc.isEditable() )
				handler.updated(doc, price);
		}
	}
}
