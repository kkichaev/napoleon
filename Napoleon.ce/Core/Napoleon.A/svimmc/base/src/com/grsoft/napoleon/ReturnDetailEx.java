package com.grsoft.napoleon;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.util.Util;
import com.grsoft.util.WaitDlg;

public class ReturnDetailEx extends ReturnDetail {
	
	HashMap<String, DiscountItem> discounts;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	private static final int REQUEST_ENABLE_BT = 1;
	private ImageButton btnPrint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvMinSum).setVisibility(View.GONE);
		btnPrint = (ImageButton) findViewById(R.id.btnPrint);
		
		ReturnEx re = (ReturnEx) doc.getData();
		discounts = DiscountImpl.loadFromDogovor(re.iddog);
		
		btnLines.setVisibility(View.GONE);
		if( !linesController.isVariable() )
			linesController.setVariable();
		
		btnSend.setVisibility(View.GONE);
		
		btnPrint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, File>() {

					@Override
					protected File doInBackground(Void... params) {
						return NPrinter.print(ReturnDetailEx.this, "returns",
								new OrderDataPrint(doc.getData()));
					}

					protected void onPreExecute() {
						showDialog(R.id.wait_dlg);
					};

					protected void onPostExecute(File result) {
						dismissDialog(R.id.wait_dlg);

						if (result != null)
							NPrinter.sendPrintTask(ReturnDetailEx.this, result);
					};

				}.execute((Void[]) null);
			}
		});
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)) {
				if (bluetoothAdapter == null)
					Toast.makeText(context, "Bluetooth недоступен",
							Toast.LENGTH_LONG).show();
				else {
					fileName = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent,
								REQUEST_ENABLE_BT);
					} else {
						printing();
					}
				}
			}
		}
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
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			
			super.drawInternal(view, name, color, item);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);

			ReturnItem ri = (ReturnItem)item; 
			String text = name;
			if( ri.number.length() > 0 ) {
				DiscountItem disc = discounts.get(ri.discid);
				
				text += "<br><b>" + ri.number + "</b> " + Util.simpleDateFormat.format(ri.date);
				if( disc != null ) {
					text += " <i>" + disc.name + "</i>";
				}
			}
			tvName.setText(Html.fromHtml(text));
			tvName.setTextColor(color);
		}
	}
	
	protected void setContentView(){
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return WaitDlg.createDialog(this);
		return super.onCreateDialog(id);
	}
}
