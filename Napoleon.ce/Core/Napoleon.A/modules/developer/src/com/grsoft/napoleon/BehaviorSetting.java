package com.grsoft.napoleon;

import android.widget.CheckBox;
import android.widget.Spinner;
import com.grsoft.napoleon.util.CfgNpl;


public class BehaviorSetting extends BehaviorSettingW {
	private Spinner spClientsAndGoods;
	private CheckBox cbKeepAway;
	
	@Override protected int getContentViewID() { return R.layout.behavior_setting_new; }
	
	@Override
	protected void init() {
		super.init();
		spClientsAndGoods = (Spinner) findViewById(R.id.spClientsAndGoods);
		cbKeepAway = (CheckBox) findViewById(R.id.cbKeepAway);
		
		CfgNpl cfex = (CfgNpl) config;
		
		if(spClientsAndGoods != null){
			if(cfex.onlyNewstItems < spClientsAndGoods.getCount())
				spClientsAndGoods.setSelection(cfex.onlyNewstItems, true);
		}
		
		if(cbKeepAway != null)
			cbKeepAway.setChecked(cfex.keepAwayInOrder);
	}
	
	@Override
	public void save() {
		CfgNpl cfex = (CfgNpl) config;
		
		if(spClientsAndGoods != null)
			cfex.onlyNewstItems = spClientsAndGoods.getSelectedItemPosition();
		
		if(cbKeepAway != null)
			cfex.keepAwayInOrder = cbKeepAway.isChecked();
		
		super.save();
	}
}
