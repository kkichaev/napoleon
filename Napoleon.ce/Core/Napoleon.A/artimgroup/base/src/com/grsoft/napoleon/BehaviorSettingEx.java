package com.grsoft.napoleon;

import com.grsoft.util.CfgNplEx;

import android.widget.EditText;

public class BehaviorSettingEx extends BehaviorSetting {
	EditText edUnfireRest;
	
	@Override
	protected int getContentViewID() {
		return R.layout.behavior_settingex;
	}
	
	@Override
	protected void init() {
		super.init();
		
		edUnfireRest = (EditText) findViewById(R.id.edUnfireRest);
		edUnfireRest.setText(Integer.toString(((CfgNplEx)config).unfire_rest));
	}
	
	@Override
	public void save() {
		try{
			((CfgNplEx)config).unfire_rest = Integer.parseInt(edUnfireRest.getText().toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		super.save();
	}
}
