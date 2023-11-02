package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.NoOrderReason;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.DisabledFirms;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.gps.GPSUtilNew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class ScriptEditEx extends ScriptEdit implements DisabledFirms.Handler {
	
	static int GPS_VALID_INTERVAL = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 10; //10 мин

	private static final int SET_NO_ORDER_REASON = 0;
	private static final int GPS_VALID = 4;
	private static final int GPS_INVALID = 5;
	private static final int WAIT_GPS_DLG_ID = 6;
	private static final int REMOVE_GPS_DLG_ID = 8;
	private static final int GPS_POS_RECIEVED = 9;

	boolean doSend = false, allowCreateDocWhithoutGpsPos = false;
	
	private WaitGpsTimerSc waitGpsTimer;
	private Timer gpsObserver;
	
	boolean tracking = false;
	
	
	@Override protected int getLayoutid() { return R.layout.script_edit_ex; }
	
	boolean NeedSetOrderReason() {
		if(doc.isEditable() && doc.isContainsItem()) {
			String orderDoc = OrderDoc.instance().getObjectName();
			for(ScriptItem si : doc.getData().items) {
				if(si.type.equals(orderDoc) ) {
					if(si.state != ScriptItem.DOC_INITED)
						return (((ScriptEx)doc.getData()).noOrderReason.length() == 0);
					else {
						if(((ScriptEx)doc.getData()).noOrderReason.length() != 0) {
							((ScriptEx)doc.getData()).noOrderReason = "";
							doc.write();
						}
						break;
					}
				}
					
			}
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if(gpsObserver != null){
			gpsObserver.cancel();
			gpsObserver = null;
		}		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (NapoleonServiceW.isTracking()){
			tracking = true;
			gpsObserver = new Timer();
			gpsObserver.scheduleAtFixedRate(new TimerTask() {
			
				@Override
				public void run() {
					Log.d(Consts.D_TAG, "gpsGuard.scheduleAtFixedRate");
					
					if (waitGpsTimer != null)
						return;
					
					boolean gpsValid = isGpsPosValid();
					
					if (gpsValid)
						handler.sendEmptyMessage(GPS_VALID);
					else
						handler.sendEmptyMessage(GPS_INVALID);
					
				}
			}, Consts.ONE_SECOND, Consts.ONE_SECOND);
		} else
			findViewById(R.id.btnGpsStatus).setVisibility(View.GONE);
	}
	
	protected boolean isGpsPosValid(){
		return GPSUtilNew.isGpsPosValid(GPS_VALID_INTERVAL);
	}
	
	protected boolean isGPSTurnOn(){
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		return locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
	}

	protected void docOpenning(int position) {
		if (chatterProtect.check()) {
			if(tracking && !doc.isDone(position)) {
				if(!allowCreateDocWhithoutGpsPos && !isGpsPosValid()) {
					if( isGPSTurnOn() == false ) 
						showDialog(R.id.ask_for_open_gps);
					else 
						Toast.makeText(this, R.string.gpscoord_is_old, Toast.LENGTH_LONG).show();					
					return;
				}
			}			
			doc.openDoc(ScriptEditEx.this, position, def.getData());
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.d(Consts.D_TAG, "handler.handleMessage: " + 
					Integer.toString(msg.what));
			switch(msg.what){
			case REMOVE_GPS_DLG_ID:
				waitGpsTimer = null;
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(ScriptEditEx.this, R.string.allow_crete_doc_without_gps, Toast.LENGTH_LONG).show();
				allowCreateDocWhithoutGpsPos = true;
				break;
			case GPS_POS_RECIEVED:
				waitGpsTimer = null;
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(ScriptEditEx.this, R.string.gps_received, Toast.LENGTH_LONG).show();
				break;
			case GPS_VALID:
				findViewById(R.id.btnGpsStatus).setVisibility(View.GONE);
				break;
			case GPS_INVALID:
				findViewById(R.id.btnGpsStatus).setVisibility(View.VISIBLE);
				break;
			}
		};
	};
	
	
	ProgressDialog pd = null;
	private void checkFirmDisable() {
		pd = ProgressDialog.show(this, "Подождите, пожалуйста", "Проверка запрета отправки");
		DisabledFirms.loadDisabledFirms(this, this);
	}

	void closeWaitDialog() {
		if( pd != null ) {
			pd.dismiss();
			pd = null;
		}
	}
	
	@Override
	public void send() {
		if(NeedSetOrderReason()) {
			showDialog(SET_NO_ORDER_REASON);
			doSend = true;
			return;
			
		}
		checkFirmDisable();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnGpsStatus).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if( isGPSTurnOn() == false ) 
					showDialog(R.id.ask_for_open_gps);
				else
					doGPSScan();
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.ask_for_location_permission)
			return CommonDialogs.createAskForPermissionDialog(this);
		
		if(id == WAIT_GPS_DLG_ID) {
			Dialog result = ProgressDialog.show(this, "", getString(R.string.wait_connection_gps));
			result.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					Log.d(Consts.D_TAG, "ProgressDialog.onKey: keyCode=" + Integer.toString(keyCode));
					
					if (keyCode == KeyEvent.KEYCODE_BACK){
						if (waitGpsTimer != null)
							waitGpsTimer.cancel();
						
						removeDialog(WAIT_GPS_DLG_ID);
					}
					
					return true;
				}
			});
			return result;
		}
		
		if(id == SET_NO_ORDER_REASON) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			
			b.setTitle("Причина отсутствия заказа");
			
			final List<NoOrderReason> src = new ArrayList<NoOrderReason>();
			DataTraveler.travel(NoOrderReason.class, new DataTraveler.Travel<NoOrderReason>(true) {

				@Override
				public boolean travel(DataTraveler<NoOrderReason> item) {
					src.add(item.data);
					return true;
				}
			}, "", "name");
			
			
			CharSequence[] cs = new CharSequence[src.size()];
			for(int i=0; i <src.size(); i++) {
				cs[i] = src.get(i).name;
			}
			b.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((ScriptEx)doc.getData()).noOrderReason = src.get(which).id;
					doc.write();
					dialog.dismiss();
					if(doSend)
						ScriptEditEx.super.send();
					else
						finish();
				}
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		if(NeedSetOrderReason()) {
			showDialog(SET_NO_ORDER_REASON);
			return;
		}
			
		super.onBackPressed();
	}

	@Override
	public void firmsLoaded(final HashSet<String> disabledFirms) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				if( disabledFirms.size() > 0 )
					Toast.makeText(ScriptEditEx.this, "Включена блокировка передачи, заявки могут не отправиться", Toast.LENGTH_SHORT).show();
				((ScriptImplEx)doc).setDisabledFirms(disabledFirms);
				DocType.setCurDoc(ScriptDoc.instance());
				ScriptEditEx.super.send();
			}
		});
	}

	@Override
	public void error(final String message) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				String err = "Ошибка проверки\n" + message;
				Toast.makeText(ScriptEditEx.this, err, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void doGPSScan() {
		GPSUtilNew.stop(this);
		GPSUtilNew.start(this);
		allowCreateDocWhithoutGpsPos = false;
		showDialog(WAIT_GPS_DLG_ID);
		waitGpsTimer = new WaitGpsTimerSc();
		waitGpsTimer.setHandler(handler);
	}
	
	class WaitGpsTimerSc extends Timer{
		private final int DELAY_TIME = 1000;
		private final int WAIT_TIME = ((CfgNplW)ConfigManager.getConfig())
				.waitGpsCoordOnRequest;
		private WGTimerTask task = new WGTimerTask();
		private Handler handler;
		private int counter; 
		
		public WaitGpsTimerSc(){
			scheduleAtFixedRate(task, DELAY_TIME, DELAY_TIME);
		}
		
		private void setHandler(Handler handler){
			this.handler = handler;
		}
		
		class WGTimerTask extends TimerTask{

			@Override
			public void run() {
				if (GPSUtilNew.isGpsPosValid(GPS_VALID_INTERVAL)){
					handler.sendEmptyMessage(GPS_POS_RECIEVED);
					cancel();
				} else if (counter >= WAIT_TIME){
					handler.sendEmptyMessage(REMOVE_GPS_DLG_ID);
					cancel();
				}
				counter += DELAY_TIME;
			}
		}
	}
}
