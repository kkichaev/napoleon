package com.grsoft.napoleon;

import android.widget.Adapter;

import com.grsoft.napoleon.util.CfgNpl;

public class BehaviorSettingEx extends BehaviorSetting {
	protected int getContentViewID() {
		return R.layout.behavior_settingex;
	}
	
	protected void applayRecreatePeriod() {
		config.monthsToRecreate = 0;

		if (spMonthRecreate != null)
			config.daysToRecreate = Integer
					.parseInt((String)spMonthRecreate.getSelectedItem());
		else
			config.daysToRecreate = 7;
	}
	
	protected void initRecreatePeriod() {
		if(spMonthRecreate != null){
			Adapter vda = spMonthRecreate.getAdapter();
			
			if (vda != null){
				for(int i = 0; i < vda.getCount(); i ++){
					if(vda.getItem(i).toString()
							.equals(Integer.toString(((CfgNpl)config).daysToRecreate))){
						spMonthRecreate.setSelection(i,  true);
						break;
					}
				}
			}
		}
	}
}
