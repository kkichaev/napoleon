package com.grsoft.napoleon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.ScrollView;
import android.widget.TextView;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.NetworkAsyncTask;

public class UpdateDBEx extends UpdateDB {

	final static String SYNC_FILE_NAME = "_sync.dat";  
	
	@Override
	protected int getContentView() { return  R.layout.updatedbex; }
	
	Date getLastSync() {
		Date res = null;
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(openFileInput(SYNC_FILE_NAME)));
			String str = r.readLine();
			long v = Long.parseLong(str);
			r.close();
			res = new Date(v);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();

		String info = "";
		Date d = getLastSync();
		if( d != null ) {
			SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			info += "Последняя синхронизация: " + f.format(d);
		}
		
		ConfigImpl c = new ConfigImpl();
		Config cfg = c.getData();
		cfg.key = "ДатаСинхрониазацииОстатков";
		if( c.read() ) {
			if( info.length() > 0 ) info += "\n";
			info += "Остатки на: " + cfg.value;
		}
		cfg.key = "ДатаСинхрониазацииДолгов";
		if( c.read() ) {
			if( info.length() > 0 ) info += "\n";
			info += "Взаиморасчёты на: " + cfg.value;
		}
		c.close();
		
		TextView tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(info);
	
		scrollView = (ScrollView)findViewById(R.id.svScroll);
		scrollView.post(new Runnable() {				
			@Override public void run() { scrollView.fullScroll(ScrollView.FOCUS_DOWN); }
		});
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(SYNC_FILE_NAME, MODE_PRIVATE));
			Date d = new Date();
			osw.write(((Long)d.getTime()).toString());
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
