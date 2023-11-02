package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.network.exception.RuntimeException;

import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	static boolean cbRest = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(cbRest);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRest = cbRemains.isChecked();
		return super.getGenDataHitchings();
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbDebt = (CheckBox) findViewById(R.id.cbDebt);
		
		if(cbDebt != null)
			cbDebt.setChecked(true);
	}
}
