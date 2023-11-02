package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.Spinner;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.ConfigImplEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override protected int getContentViewID() { return R.layout.warehouse_settingex; }


	@Override
	protected void init() {
		super.init();
		
		ConfigImpl config = new ConfigImpl();
		Spinner sp;

		sp = (Spinner)findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), sp, ((ConfigImplEx)this.config).selectedFirm);

		sp = (Spinner)findViewById(R.id.spWH);
		DialogHelper.loadSpinnerFromConfig(config, "Склады", new ArrayList<CharSequence>(), sp, ((ConfigImplEx)this.config).selectedPrice);

		config.close();
	}
	
	@Override
	public void save() {
		super.save();
		
		Spinner sp;
		sp = (Spinner)findViewById(R.id.spFirma);
		((ConfigImplEx)this.config).selectedFirm = sp.getSelectedItemPosition();

		sp = (Spinner)findViewById(R.id.spWH);
		((ConfigImplEx)this.config).selectedPrice = sp.getSelectedItemPosition();		
	}
}
