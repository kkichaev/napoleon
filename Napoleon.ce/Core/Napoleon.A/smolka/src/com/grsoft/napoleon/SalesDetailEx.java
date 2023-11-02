package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class SalesDetailEx extends SalesDetail{
	private static final String TAG = "SalesDetailEx";
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnLines.setVisibility(View.GONE);
	}
	
	@Override
	protected void addPrintItems(List<String> items) {
		items.add("Накладная");		
	}
		
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");
			
		    String action = intent.getAction();
	    	Log.d(TAG, action);

	        if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)){
				if (bluetoothAdapter == null) 
				   Toast.makeText(context, "Bluetooth недоступен", Toast.LENGTH_LONG).show();
				else{
					fileName  = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					} else {
						printing();						
					}
				}
	        }
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	};
	
	protected void printing() {
		BTPrinterSettings cfg = BTPrinterHelper.getSettings(this);
		if( cfg.address.length() > 0 )
			BTPrinterHelper.printing(cfg.address, cfg.copies, fileName, this);
		else {
			Toast.makeText(this, "Настройте, пожалуйста, принтер", Toast.LENGTH_SHORT).show();
			Setting.open(this, TextPrinterSetting.class);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
			printing();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeItem(MNU_PKO_ID);
		
		return true;
	}
}