package com.grsoft.napoleon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.WiFiPrint;
import com.grsoft.napoleon.util.WiFiPrinterConfig;

public class SalesDetailEx extends SalesDetail {

	String fileName;
	
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
			WiFiPrint.print(cfg, this, fileName);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
}
