package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNpl;

import android.widget.CheckBox;

public class BehaviorSettingEx extends BehaviorSetting {
	@Override protected int getContentViewID() { return R.layout.behavior_settingex; }
	
	@Override
	protected void init() {
		super.init();
		
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbSaveReports);
		cb.setChecked(((CfgNpl)config).saveReportsToCard);
	}
	
	@Override
	public void save() {
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbSaveReports);
		((CfgNpl)config).saveReportsToCard = cb.isChecked(); 
		super.save();
	}
}
