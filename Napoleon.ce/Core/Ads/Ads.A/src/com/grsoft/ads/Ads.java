package com.grsoft.ads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.impl.ClientImpl;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.documents.AdapterListDocType;
import com.grsoft.ads.documents.OrderDoc;
import com.grsoft.ads.utils.gps.WorkDayTracking;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.Napoleon;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.Util;

public class Ads extends UpdateActivity{
	protected UpdateProcess updateProcess;
	protected ListView lvOrders;
	private BroadcastReceiver broadcatsReceiver; 
	protected AdapterListDocType docType = (AdapterListDocType) OrderDoc.instance();
	public static final String UPDATE_ACTION = "com.grsoft.ads.UPDATE_ACTION";
	private static final int ASK_PASSWORD_DLG = 0;
	private static final int REJECT_CAUSE_DLG = 1;
	protected static final int INPUT_OWN_CAUSE_DLG = 2;
	public static Class<? extends Service> serviceType = AdsService.class;
	
	public static final String ADMPWD = "ADMPWD";
	
	private long selOrderRowid = ExtrasConst.INVALID_ID;
	private final String SEL_ORDER_ROWID = "sel_order_rowid";
	private TextView tvDistance;
	private BroadcastReceiver locationChangedReceiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this));

		lvOrders = (ListView)findViewById(R.id.lvOrders);
        registerForContextMenu(lvOrders);
        
        Intent intent = new Intent(this, serviceType);
		getApplicationContext().startService(intent);
		
		broadcatsReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				docType.setListControls(Ads.this, lvOrders, null);
				Toast.makeText(context, 
						"Выполнена синхронизация с базой данных", 
						Toast.LENGTH_LONG).show();
			}
		};
		
		findViewById(R.id.btnMessage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Messages.open(v.getContext());
			}
		});
		
		findViewById(R.id.btnUserOrder).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserOrders.open(v.getContext());
			}
		});
		
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		
		locationChangedReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				int distance = intent.getIntExtra(WorkDayTracking.DISTANCE, 0);
				setDistanceFmt(distance);
			}
		};
    }

	protected int getLayoutId() {
		return R.layout.ads;
	}
    
    private void setDistanceFmt(int distance) {
		if (distance < 1000)
			tvDistance.setText(String.format("%d, м", distance));
		else
			tvDistance.setText(String.format("%d, км", distance / 1000));
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main_opt_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (WorkDayTracking.isCanStart()){
    		menu.findItem(R.id.itStartWorking).setVisible(true);
    		menu.findItem(R.id.itEndWorking).setVisible(false);
    	}else{
    		menu.findItem(R.id.itStartWorking).setVisible(false);
    		menu.findItem(R.id.itEndWorking).setVisible(true);
    	}
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId(); 
    	if (itemId == R.id.itConfig){
    		ConfigImpl config = new ConfigImpl();
    		config.getData().key = ADMPWD;
    		boolean readResult = config.read();
    		config.close();
    		
    		if (readResult)
    			showDialog(ASK_PASSWORD_DLG);
    		else
    			SettingMainPage.open(this);
    		
    	} else if (itemId == R.id.itUpdate) {
    		fireUpdate(this);
    	} else if (itemId == R.id.itClose) {
    		Intent intent = new Intent(this, serviceType);
			boolean stopped = stopService(intent);
			Log.d(Consts.D_TAG, "Service has been stopped:" + Boolean.toString(stopped));
			finish();
    	} else if (itemId == R.id.itUserOrder)
    		UserOrders.open(this);
    	else if (itemId == R.id.itStartWorking){
    		tvDistance.setVisibility(View.VISIBLE);
    		setDistanceFmt(WorkDayTracking.startWorking());
    	}else if (itemId == R.id.itEndWorking){
    		tvDistance.setVisibility(View.GONE);
    		WorkDayTracking.endWorking();
    	}else if (itemId == R.id.itMessages)
    		Messages.open(this);
    	else if (itemId == R.id.itAbout)
    		Napoleon.showAbout(this);

    	return true;
    }

	protected void fireUpdate(final Context context) {
		updateProcess = UpdateProcess.createProcess(this, this);
		updateProcess.setPostWorker(new Runnable() {
			
			@Override
			public void run() {
				docType.setListControls(context, lvOrders, null);
				getSharedPreferences(
						Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
							edit().putBoolean(Setting.CLEAR, false).commit();
			}
		});
		updateProcess.execute((Void[])null);
	}
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	if (docType != null)
    		docType.close();
    	
    	if (updateProcess != null)
    		updateProcess.cancel(false);
    	
    	unregisterReceiver(broadcatsReceiver);
    	unregisterReceiver(locationChangedReceiver);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		DocType.setCurDoc(OrderDoc.instance());
		
		boolean cleaned = getSharedPreferences(
				Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
					getBoolean(Setting.CLEAR, false);
		
		boolean orderRestored = getSharedPreferences(
				Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
					getBoolean(Setting.RECREATEORDER, false);
		
		if (!cleaned && !orderRestored && 
				lvOrders.getAdapter() != null)
			((BaseAdapter)lvOrders.getAdapter())
				.notifyDataSetChanged();
		else
			docType.setListControls(this, lvOrders, null);
		
		registerReceiver(broadcatsReceiver, new IntentFilter(UPDATE_ACTION));
		registerReceiver(locationChangedReceiver, new IntentFilter(WorkDayTracking.ON_LOCATION_CHANGE_ACTION));
		
		if (WorkDayTracking.isWorkingTime()){
			setDistanceFmt(WorkDayTracking.getDistance());
			tvDistance.setVisibility(View.VISIBLE);
		}else
			tvDistance.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Toast.makeText(this, "Для выхода из программы выберите Меню -> Выход.", 
					Toast.LENGTH_LONG).show();
			
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Order order = getSelectedOrder(menuInfo);
		
		if (order != null){
			getMenuInflater()
				.inflate(getContextMenuId(), menu);
			
			MenuItem itRejectCause =  menu.findItem(R.id.itRejectCause);
			itRejectCause.setVisible((order.params & Order.DONE_PARAMS) == 0 );
		}
	}
	
	protected int getContextMenuId() {
		return R.menu.main_context_menu;
	}
	
	protected Order getSelectedOrder(ContextMenuInfo menuInfo){
		Object selectedItem = docType
				.getSelectedItem(
						((AdapterContextMenuInfo)menuInfo).position);
		
		Order result = null;
		
		if (selectedItem != null &&
				selectedItem instanceof OrderImpl){
			
				OrderImpl orderImpl = (OrderImpl)selectedItem;
				selOrderRowid = orderImpl.getRowid();
				result = orderImpl.getData();
		}
		
		return result;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId(); 
		if (itemId == R.id.itMap) {
			Order order = getSelectedOrder(item.getMenuInfo());
			
			if (order != null)
				Map.open(this, order.client);
		} else if (itemId == R.id.itRejectCause)
			showDialog(REJECT_CAUSE_DLG);
		else if (item.getItemId() == R.id.itMapEx){
			Order order = getSelectedOrder(item.getMenuInfo());
			
			if (order != null && order.client != null){
				ClientImpl ci = new ClientImpl();
				ci.getData().id = order.client;
				
				if (ci.read()){
					String uri = String
							.format("geo:0,0?q=%s", ci.getData().address );
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(intent);
				}
			}
		} 
		
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case ASK_PASSWORD_DLG:
			return createAskPasswordDlg();
		case REJECT_CAUSE_DLG:
			return createRejectCauseDlg();
		case INPUT_OWN_CAUSE_DLG:
			return createOwnCauseDlg();
		default: return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case ASK_PASSWORD_DLG:
			EditText edPassword = (EditText) dialog.findViewById(R.id.edPassword);
			edPassword.setText("");
			break;
		}
	}
	
	private Dialog createOwnCauseDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Причина отказа");
		builder.setMessage("Введите текст.");
		builder.setView(View.inflate(this, R.layout.input, null));
		builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String cause = ((EditText)((AlertDialog)dialog).findViewById(R.id.edInput)).getText().toString();
				setRejectCause(cause);	
			}
		});
		
		builder.setNegativeButton("Отменить", null);
		
		return builder.create();
	}

	private Dialog createRejectCauseDlg() {
		final String OWN_CAUSE_STR = "Своя причина"; 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.reject_cause, null);
		builder.setView(view);
		builder.setNegativeButton("Закрыть", null);
		builder.setTitle("Выберите причину отказа");
		final AlertDialog result = builder.create();
		ConfigImpl configImpl = new ConfigImpl();
		
		final String REJECT_CAUSE = "REJECT_CAUSE";
		configImpl.getData().key = REJECT_CAUSE;
		configImpl.read();
		configImpl.close();
		String causes = configImpl.getData().value;
		
		List<String> list = new ArrayList<String>();
		
		if (causes.length() > 0)
			list.addAll(Arrays.asList(causes.split(";")));
		
		list.add(OWN_CAUSE_STR);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		ListView listView = (ListView)view.findViewById(android.R.id.list); 
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(getClass().getName(), "onItemClick");
				String cause = ((TextView)view).getText().toString();
				
				if (cause.equals(OWN_CAUSE_STR))
					showDialog(INPUT_OWN_CAUSE_DLG);
				else{
					setRejectCause(cause);	
				}
					
				result.dismiss();
			}
		});
		
		return result;
	}

	protected Dialog createAskPasswordDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.input_passw, null));
		builder.setTitle("Настройки");
		builder.setMessage("Введите пароль");
		builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ConfigImpl configImpl = new ConfigImpl();
				configImpl.getData().key = ADMPWD;
				
				boolean passwordOK = false;
				if (configImpl.read()){
					EditText edPassword = (EditText) ((AlertDialog)dialog).findViewById(R.id.edPassword);
				    passwordOK = edPassword.getText().toString().equals(configImpl.getData().value); 
				}
				
				configImpl.close();
				
				if (passwordOK)
					SettingMainPage.open(Ads.this);
				else
					SettingUser.open(Ads.this);
			}
		});
		
		builder.setNegativeButton("Отменить", null);
		return builder.create();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(SEL_ORDER_ROWID, selOrderRowid);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		selOrderRowid = savedInstanceState.getLong(SEL_ORDER_ROWID, 
				ExtrasConst.INVALID_ID);
	}

	protected void setRejectCause(String cause) {
		OrderImpl orderImpl = new OrderImpl();
		
		if (orderImpl.read(selOrderRowid)){
			orderImpl.setDone();
			Date date = Util.getDateTime();
			orderImpl.setRejected();
			orderImpl.getData().factend = date;
			orderImpl.getData().factbegin = date;
			orderImpl.getData().remark = cause;
			
			orderImpl.write();
			
			if (lvOrders.getAdapter() != null)
				((BaseAdapter)lvOrders.getAdapter())
					.notifyDataSetChanged();
		}
		
		orderImpl.close();
	}
}

