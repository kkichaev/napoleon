package com.grsoft.napoleon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class SalesDetailEx extends SalesDetail {
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";

	@Override
	protected String[] createPrintCaption() {
		return new String[] { getString(R.string.delivery) };
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
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
			Setting.open(this, PrinterSetting.class);
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
	protected void updateTotalSum() {
		super.updateTotalSum();

		TextView tv = (TextView) findViewById(R.id.tvInfo);
		int sum = doc.sum();

		SalesEx oe = (SalesEx) doc.getData();
		OrgDogovor dog = DocHelper.getDogovor((OrgEx)org.getData(), oe.iddog);

		sum -= (int) (((long) sum * oe.discval + Consts.SUM_SCALE
				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

		tv.setText(String.format("%s(%s%%)",
				Util.IntToScaleStr(sum, Consts.SUM_SCALE),
				Util.IntToScaleStr(oe.discval, Consts.SUM_SCALE)));

		if( dog != null ) {
			tv = (TextView) findViewById(R.id.tvMinSum);
			tv.setText(getString(R.string.min_order_cost, Util.IntToScaleStr(
					dog.minOrder, Consts.SUM_SCALE)));
		}
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetailex);
	}
}
