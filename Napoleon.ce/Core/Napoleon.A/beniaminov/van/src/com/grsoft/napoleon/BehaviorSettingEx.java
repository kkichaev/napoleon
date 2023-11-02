package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;
import android.widget.CheckBox;

public class BehaviorSettingEx extends BehaviorSetting {
	CheckBox cbSimpleMode;
	
	@Override
	protected void init() {
		super.init();
		cbSimpleMode = (CheckBox) findViewById(R.id.cbSimpleMode);
		cbSimpleMode.setChecked(((CfgNplEx)config).simpleMode);
	}
	
	@Override
	public void save() {
		((CfgNplEx)config).simpleMode = cbSimpleMode.isChecked(); 
		super.save();
	}
	
	@Override protected int getContentViewID() { return R.layout.behavior_settingex; }
}
