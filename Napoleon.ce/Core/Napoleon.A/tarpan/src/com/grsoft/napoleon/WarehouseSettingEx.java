package com.grsoft.napoleon;

import com.grsoft.napoleon.util.ConfigManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.CheckBox;
import android.widget.TimePicker;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override
	protected int getContentViewID() {
		return R.layout.warehouse_settingex;
	}

	@Override
	protected void init() {
		super.init();
		SharedPreferences pref = getApplication().getSharedPreferences(BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbBuh);
		cb.setChecked(pref.getBoolean(BehaviorSettingEx.BUH_KEY, false));
		cb = (CheckBox) findViewById(R.id.cbVibrate);
		cb.setChecked(config.vibration);
		cb = (CheckBox) findViewById(R.id.cbAllowRotateScreen);
		cb.setChecked(config.allowRotateScreen);
		
		TimePicker tp = (TimePicker) findViewById(R.id.tpFrom);
		tp.setIs24HourView(true);
		tp.setCurrentHour(pref.getInt(BehaviorSettingEx.TIME_FROM_HOUR, BehaviorSettingEx.DEF_HOUR_FROM));
		tp.setCurrentMinute(pref.getInt(BehaviorSettingEx.TIME_FROM_MIN, BehaviorSettingEx.DEF_MIN_FROM));
		tp = (TimePicker) findViewById(R.id.tpTo);
		tp.setIs24HourView(true);
		tp.setCurrentHour(pref.getInt(BehaviorSettingEx.TIME_TO_HOUR, BehaviorSettingEx.DEF_HOUR_TO));
		tp.setCurrentMinute(pref.getInt(BehaviorSettingEx.TIME_TO_MIN, BehaviorSettingEx.DEF_MIN_FROM));
	}
	
	@Override
	public void save() {
		CheckBox cb = (CheckBox)findViewById(R.id.cbBuh);
		Editor ed = getApplication().getSharedPreferences(BehaviorSettingEx.SETING_NAME, 
				Context.MODE_PRIVATE).edit();
		ed.putBoolean(BehaviorSettingEx.BUH_KEY, cb.isChecked());
		
		TimePicker tp = (TimePicker) findViewById(R.id.tpFrom);
		ed.putInt(BehaviorSettingEx.TIME_FROM_HOUR, tp.getCurrentHour());
		ed.putInt(BehaviorSettingEx.TIME_FROM_MIN, tp.getCurrentMinute());
		tp = (TimePicker) findViewById(R.id.tpTo);
		ed.putInt(BehaviorSettingEx.TIME_TO_HOUR, tp.getCurrentHour());
		ed.putInt(BehaviorSettingEx.TIME_TO_MIN, tp.getCurrentMinute());
		
		ed.commit();
		
		cb = (CheckBox) findViewById(R.id.cbVibrate);
		config.vibration = cb.isChecked();
		
		cb = (CheckBox) findViewById(R.id.cbAllowRotateScreen);
		config.allowRotateScreen = cb.isChecked();
		
		config.priceClmn2Type = getColumnType(spColumn2.getSelectedItemPosition());
		config.priceClmn3Type = getColumnType(spColumn3.getSelectedItemPosition());

		cb = (CheckBox) findViewById(R.id.cbPackView);
		if( cb != null )
			config.isPackView = cb.isChecked();

		if( Features.ID_COLUMN_IN_PRICE_LIST) {
			cb = (CheckBox)findViewById(R.id.cbItemID);
			if( cb != null )
				config.idInPriceList = cb.isChecked();
		}
		
		ConfigManager.save();
	}
}
