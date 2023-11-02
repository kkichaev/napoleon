package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.util.CfgNplEx;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class BehaviorSettingEx extends BehaviorSetting {
	CheckBox cbSimpleMode;
	
	@Override
	protected void init() {
		super.init();
		CfgNplEx ce = (CfgNplEx)config;
		
		cbSimpleMode = (CheckBox) findViewById(R.id.cbSimpleMode);
		cbSimpleMode.setChecked(ce.simpleMode);
		
		Spinner sp = (Spinner)findViewById(R.id.spSimpleModeColumns);
		int selected = 0;
		List<String> values = new ArrayList<String>();
		for(int i=1; i < 5; i++) {
			if( i == ce.simpleModeColumns )
				selected = values.size();
			values.add(Integer.toString(i));
		}

		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(aa);
		sp.setSelection(selected);		
	}
	
	@Override
	public void save() {
		CfgNplEx ce = (CfgNplEx)config;

		ce.simpleMode = cbSimpleMode.isChecked();
		ce.simpleModeColumns = ((Spinner)findViewById(R.id.spSimpleModeColumns)).getSelectedItemPosition() + 1;
		
		super.save();
	
		sendBroadcast(new Intent(UpdateDBEx.RELOAD_ACTION));
	}
	
	@Override protected int getContentViewID() { return R.layout.behavior_settingex; }
}
