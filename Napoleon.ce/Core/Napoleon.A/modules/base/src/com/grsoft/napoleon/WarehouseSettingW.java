package com.grsoft.napoleon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.SettingActivity;

/***
 * Настройка поведения формы прайса
 * @author kki
 *
 */
public class WarehouseSettingW extends SettingActivity {
	protected CfgNplW config;
	protected Spinner spColumn2;
	protected Spinner spColumn3;
	private Spinner spNavType;
	private boolean serviceBound = false;
	private NapoleonServiceW napoleonService;
	
	protected int getContentViewID() { return R.layout.warehouse_setting; }
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			napoleonService = ((NapoleonServiceW.LocalBinder) service)
					.getService();
			serviceBound = true;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		
		init();
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, Napoleon.serviceType);
		boolean bindResult = getApplicationContext().
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.d(getClass().getCanonicalName(), 
				String.format("onStart: bindService %s", Boolean.toString(bindResult)));
		
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		if (serviceBound) {
			getApplicationContext().unbindService(serviceConnection);
			serviceBound = false;
		}
	}
	
	/**
	 * При изменении списка типа колонок надо корректно установить спиннер
	 * @param sp
	 * @param value
	 */
	protected void setSpinner(Spinner sp, int value) {
		if( sp != null )
			sp.setSelection(value);
	}
	
	protected void init() {
		config = (CfgNplW) ConfigManager.getConfig();
		spColumn2 = (Spinner) findViewById(R.id.spColumn2);
		setSpinner(spColumn2, config.priceClmn2Type);
		spColumn3 = (Spinner) findViewById(R.id.spColumn3);
		setSpinner(spColumn3, config.priceClmn3Type);
		spNavType = (Spinner) findViewById(R.id.spNavType);

		CheckBox cb = (CheckBox) findViewById(R.id.checkPrice);
		cb.setChecked(config.checkPrice);

		cb = (CheckBox) findViewById(R.id.cbComplexSalesHistory);
		cb.setChecked(config.isComplexSalesHistory);

		cb = (CheckBox) findViewById(R.id.cbPackView);
		if( cb != null )
			cb.setChecked(config.isPackView);
	
		if( Features.ID_COLUMN_IN_PRICE_LIST) {
			View v = findViewById(R.id.trItemID);
			if( v != null )
				v.setVisibility(View.VISIBLE);
			
			cb = (CheckBox)findViewById(R.id.cbItemID);
			if(cb != null) {
				cb.setChecked(config.idInPriceList);
				cb.setVisibility(View.VISIBLE);
			}
		}
		
		if(spNavType != null && config.isNewPriceNavType)
			spNavType.setSelection(1);
		
		if(Features.UPDATE_PRICE_BACKGROUND){
			initUpdatePriceControls();
		}
		
		Spinner sp = (Spinner) findViewById(R.id.spPriceLevelCount);
		setSpinner(sp, config.priceLevel);
	}

	protected void initUpdatePriceControls() {
		CheckBox cb = (CheckBox) findViewById(R.id.cbUseUpdatePrice);
		
		if(cb != null){
			cb.setVisibility(View.VISIBLE);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					View v = findViewById(R.id.spUpdatePriceInBg);
					
					if(v != null)
						v.setEnabled(isChecked);
				}
			});
			cb.setChecked(config.useUpdatePrice);
		}
		
		Spinner sp = (Spinner) findViewById(R.id.spUpdatePriceInBg);
		if(sp != null){
			sp.setVisibility(View.VISIBLE);
			
			String[] send_interval = getResources().getStringArray(R.array.gps_send_interval);
			int pos = 0;
		
			try {
				for (pos = 0; pos < send_interval.length; pos++)
					if (config.updatePriceTime == Integer.parseInt(send_interval[pos]))
						break;
			} catch (Exception e) {}
			
			if (pos < send_interval.length)
				sp.setSelection(pos, true);
			
			sp.setEnabled(config.useUpdatePrice);
		}
	}
	
	protected int getColumnType(int spinnerValue) {
		return spinnerValue;
	}
	
	@Override
	public void save() {
		config.priceClmn2Type = getColumnType(spColumn2.getSelectedItemPosition());
		config.priceClmn3Type = getColumnType(spColumn3.getSelectedItemPosition());
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkPrice);
		config.checkPrice = cb.isChecked();

		cb = (CheckBox) findViewById(R.id.cbComplexSalesHistory);
		config.isComplexSalesHistory = cb.isChecked();

		cb = (CheckBox) findViewById(R.id.cbPackView);
		if( cb != null )
			config.isPackView = cb.isChecked();

		if( Features.ID_COLUMN_IN_PRICE_LIST) {
			cb = (CheckBox)findViewById(R.id.cbItemID);
			if( cb != null )
				config.idInPriceList = cb.isChecked();
		}
		
		if(spNavType != null)
			config.isNewPriceNavType = spNavType.getSelectedItemPosition() > 0;

		if(Features.UPDATE_PRICE_BACKGROUND){
			try {
				Spinner sp = (Spinner) findViewById(R.id.spUpdatePriceInBg);
				if(sp != null)
					config.updatePriceTime  = Integer.parseInt((String) sp.getSelectedItem());
				
				cb = (CheckBox) findViewById(R.id.cbUseUpdatePrice);
				
				if (cb != null)
					config.useUpdatePrice = cb.isChecked();
				
			} catch (Exception e) {}
		}	
		
		ConfigManager.save();
		
		if (serviceBound)
			napoleonService.update();
		
		Spinner sp = (Spinner) findViewById(R.id.spPriceLevelCount);
		
		if(sp != null){
			if(sp.getSelectedItemPosition() != config.priceLevel){
				config.priceLevel = sp.getSelectedItemPosition();
				FoldersAdapter.resetCache();
			}
		}
	}

	@Override
	public void update() {
		init();
	}

	@Override
	public int getName() {
		return R.string.price;
	}

	@Override
	public int getIcon() {
		return R.drawable.setting_warehouse;
	}

}
