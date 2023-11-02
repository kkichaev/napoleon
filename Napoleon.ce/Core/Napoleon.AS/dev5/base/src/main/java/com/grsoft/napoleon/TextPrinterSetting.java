package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class TextPrinterSetting extends SettingActivity {
	static final String TAG = "PrinterSetting";

	protected static final int WAIT_DLG = 0;

	private static final int REQUEST_ENABLE_BT = 100;
	
	private ArrayAdapter<KeyValue> devices;
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices);
		
		findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View arg0) { refreshDevices(); }
		});
		
		devices = new ArrayAdapter<KeyValue>(this, R.layout.devices_row, R.id.tvName){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null)
					convertView = View.inflate(getContext(), R.layout.devices_row, null);
				
				TextView textView = (TextView)convertView.findViewById(R.id.tvName);
				KeyValue item = getItem(position);
				if( item != null && item.value != null ){
					String text = item.value.toString();
					if(listView.isItemChecked(position)) {
						convertView.setBackgroundResource(R.drawable.device_selected);
					} else
						convertView.setBackgroundResource(R.drawable.device_back);
	//					text = "<b>" + text + "</b>";
					textView.setText(Html.fromHtml(text));
				}
				return convertView;
			}
		};
		
		listView = ((ListView)findViewById(R.id.lvDevices));
		listView.setAdapter(devices);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View view, int arg2, long arg3) {
				listView.setItemChecked(arg2, true);
				devices.notifyDataSetChanged();
			}
		});

		update();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");
			
		    String action = intent.getAction();
	    	Log.d(TAG, action);
	    	if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
	    		showDialog(WAIT_DLG);
	    		devices.clear();
	    		int pos = listView.getCheckedItemPosition();
	    		if( pos >= 0 )
	    			listView.setItemChecked(pos, false);	    		
	    	}else  if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
			try {
			    dismissDialog(WAIT_DLG);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		    	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    	KeyValue kv = new KeyValue(device.getAddress(), device.getName());
		    	
		    	boolean found = false;
		    	for( int i=0; i<devices.getCount(); i++ ) {
		    		if( devices.getItem(i).key.equals(kv.key)) {
		    			found = true;
		    			break;
		    		}
		    	}
		    	
		    	if( !found ) {
		            devices.add(kv);
		            devices.notifyDataSetChanged();
		    	}
	        }
		}
	};
		
	protected Dialog createWaitDlg() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage("Подождите...");
		
		return result;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_DLG:
			return createWaitDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	protected void refreshDevices() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if( adapter != null ) {
			if (!adapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			boolean r = adapter.startDiscovery();
			Log.d(TAG, "bluetoothAdapter.startDiscovery() = " + r);
		} else {
			Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
			boolean r = BluetoothAdapter.getDefaultAdapter().startDiscovery();
			Log.d(TAG, "bluetoothAdapter.startDiscovery() = " + r);
		}
	}
	@Override
	public void save() {
		BTPrinterSettings cfg = new BTPrinterSettings();

		int pos = listView.getCheckedItemPosition();
		if( pos >= 0 ) {
			KeyValue kv = devices.getItem(pos);
			
			if (kv != null)
			{
				cfg.address = kv.key.toString();
				cfg.name = kv.value.toString();
				
				BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
				if( ba != null ) {
					BluetoothDevice dev = ba.getRemoteDevice(cfg.address);
					if( dev.getBondState() == BluetoothDevice.BOND_NONE ) {
						Toast.makeText(this, R.string.device_no_paired, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		
		try{
			EditText ed = (EditText)findViewById(R.id.edCopies);
			cfg.copies = Integer.parseInt(ed.getText().toString());
			
			ed = (EditText) findViewById(R.id.edRowCount);
			cfg.row_count = Integer.parseInt(ed.getText().toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		BTPrinterHelper.saveSettings(cfg, this);
	}

	@Override
	public void update() {
		BTPrinterSettings cfg = BTPrinterHelper.getSettings(this);
		
		EditText ed = (EditText)findViewById(R.id.edCopies);
		ed.setText(Integer.toString(cfg.copies));
		
		ed = (EditText)findViewById(R.id.edRowCount);
		ed.setText(Integer.toString(cfg.row_count));
		
		if( cfg.address.length() > 0 ) {
			KeyValue kv = new KeyValue(cfg.address, cfg.name);
			devices.clear();
			devices.add(kv);
			devices.notifyDataSetChanged();
			listView.setItemChecked(0, true);
			devices.notifyDataSetChanged();
		}
	}

	@Override
	public int getName() {
		return R.string.print_settings;
	}

	@Override
	public int getIcon() {
		return R.drawable.print;
	}

}
