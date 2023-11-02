package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.ConfigAgama;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class WarehouseSettingEx extends WarehouseSetting {
	ArrayList<CharSequence> sklads = new ArrayList<CharSequence>();
	
	@Override
	protected int getContentViewID() {
		return R.layout.warehouse_setting_ex;
	}

	@Override
	protected void init() {
		super.init();
		
		ConfigAgama cfg = (ConfigAgama)ConfigManager.getConfig();
		if( cfg.autoVisit )
			((CheckBox)findViewById(R.id.cbAutoVisit)).setChecked(true);
		
		ConfigImpl c = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spWhDefault);
		DialogHelper.loadSpinnerFromConfig(c, "Склад", sklads, sp, cfg.whDefault);
		c.close();
	}
	
	@Override
	public void save() {
		super.save();
		
		ConfigAgama cfg = (ConfigAgama)config;
		cfg.autoVisit = ((CheckBox)findViewById(R.id.cbAutoVisit)).isChecked();

		Spinner sp = (Spinner)findViewById(R.id.spWhDefault);
		cfg.whDefault = sp.getSelectedItemPosition();
	}
}
