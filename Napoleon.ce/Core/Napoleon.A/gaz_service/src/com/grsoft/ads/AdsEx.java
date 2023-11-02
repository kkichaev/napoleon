package com.grsoft.ads;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.grsoft.ads.dataobjects.Client;
import com.grsoft.ads.dataobjects.impl.ClientImpl;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.dataobjects.impl.PauseImpl;
import com.grsoft.ads.utils.gps.WorkDayTracking;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class AdsEx extends Ads {
	protected static final int SMS_DLG = R.id.sms_dlg;
	ImageButton btnMessage;
	ImageButton btnUserOrder;
	ImageButton btnPause;
	String idClnt = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnMessage = (ImageButton) findViewById(R.id.btnMessage);
		btnUserOrder = (ImageButton) findViewById(R.id.btnUserOrder);
		btnPause = (ImageButton) findViewById(R.id.btnPause);
		btnPause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences(Setting.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE);
				boolean isPause = pref.getBoolean(Setting.PAUSE, false);
				pref.edit().putBoolean(Setting.PAUSE, !isPause).commit();
				firePause(!isPause);
			}
		});
		
		lvOrders.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				OrderImpl o = (OrderImpl) ((DataBaseAdapter<?>)parent.getAdapter()).getItem(position);
				idClnt = o.getData().client;
				showDialog(SMS_DLG);
				return true;
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SMS_DLG)
			return CreateSMSDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog CreateSMSDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.sms_send_dlg, null));
		builder.setTitle(R.string.send_sms_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SmsManager sms = SmsManager.getDefault();
				String phoneNumber = ((EditText)((Dialog)dialog).findViewById(R.id.edPhone)).getText().toString();
				String message = ((EditText)((Dialog)dialog).findViewById(R.id.edText)).getText().toString();
				
				if (phoneNumber.length() == 0 || message.length() == 0)
					Toast.makeText(AdsEx.this, R.string.need_all_field_data, Toast.LENGTH_SHORT).show();
				else{
					sms.sendTextMessage(phoneNumber, null, message, null, null);
					Toast.makeText(AdsEx.this, R.string.sms_was_sent, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == SMS_DLG)
			prepareSMDDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	
	private void prepareSMDDlg(Dialog dialog) {
		ClientImpl client = new ClientImpl();
		client.getData().id = idClnt;
		client.read();
		client.close();
		
		Client c = client.getData();
		Spinner spPhone = (Spinner) dialog.findViewById(R.id.spPhone);
		spPhone.setVisibility(View.GONE);
		EditText edPhone = (EditText) dialog.findViewById(R.id.edPhone);
		
		if(c.contacts == null || c.contacts.size() == 0)
			Toast.makeText(this, R.string.contacts_empty, Toast.LENGTH_SHORT).show();
		else if (c.contacts != null){
			if (c.contacts.size() > 1){
				String[] phone = new String[c.contacts.size()];
				
				for(int i = 0; i < c.contacts.size(); i++)
					phone[i] = c.contacts.get(i).phone;
				
				spPhone.setAdapter(new ArrayAdapter<CharSequence>(this, 
						R.layout.simple_spinner_layout, phone));
			    spPhone.setVisibility(View.VISIBLE);
			}
			
			edPhone.setText(c.contacts.get(0).phone);
		}
		
		ConfigImpl cfg = new ConfigImpl();
		cfg.getData().key = "SMSTEMP";
		cfg.read();
		cfg.close();
		
		EditText edText = (EditText) dialog.findViewById(R.id.edText);
		edText.setText(cfg.getData().value);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.adsex;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		boolean isPause = isPause();
		menu.findItem(R.id.itConfig).setVisible(!isPause);
		menu.findItem(R.id.itUpdate).setVisible(!isPause);
		menu.findItem(R.id.itUserOrder).setVisible(!isPause);
		menu.findItem(R.id.itMessages).setVisible(!isPause);
		return true;
	}
	
	private void firePause(boolean pause){
		initPauseControl(pause);
		
		PauseImpl pauseImpl = new PauseImpl();
		GpsCoord coord = GPSUtilNew.getLastKnownLocation();
		
		if(pause){
			pauseImpl.getData().pause = Util.getDateTime();
			pauseImpl.getData().plat = coord.latitude;
			pauseImpl.getData().plong = coord.longitude;
		}else{
			DbReader reader = new DbReader();
			DbWriter.checkDBTable(pauseImpl.getData().getClass());
			String table = DataObjectInfo.getInstance().getTableName(pauseImpl.getData().getClass()); 
			reader.select(pauseImpl.getData(), 
					table, 
					"rowid = (select max(rowid) from " + table+")");
			pauseImpl.getData().resume = Util.getDateTime();
			pauseImpl.getData().rlat = coord.latitude;
			pauseImpl.getData().rlong = coord.longitude;
			pauseImpl.getData().params &= ~ParamState.ofExported; 
		}
		
		pauseImpl.write();
		pauseImpl.close();
		fireUpdate(this);
	}
	
	protected void initPauseControl(boolean pause) {
		lvOrders.setEnabled(!pause);
		btnMessage.setEnabled(!pause);
		btnUserOrder.setEnabled(!pause);
		
		if(WorkDayTracking.isCanEnd()){
			btnPause.setImageResource(!pause ? R.drawable.stop : R.drawable.start);
			btnPause.setVisibility(View.VISIBLE);
		}else
			btnPause.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initPauseControl(isPause());
	}
	
	protected boolean isPause() {
		boolean isPause = getSharedPreferences(Setting.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE).getBoolean(Setting.PAUSE, false);
		return isPause;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		initPauseControl(isPause());
		return result;
	}
}
