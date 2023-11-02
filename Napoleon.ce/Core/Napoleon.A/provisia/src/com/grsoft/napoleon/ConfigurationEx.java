package com.grsoft.napoleon;

import android.widget.CheckBox;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class ConfigurationEx extends Configuration {
	@Override protected int getLayoutID() { return R.layout.configex; }
	
	@Override 
	protected void init() {
		super.init();
		
		CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();
		
		CheckBox cb;		
		cb = (CheckBox)findViewById(R.id.cbHideRest);
		cb.setChecked(cfg.hideRestQTY);
		
		cb = (CheckBox)findViewById(R.id.cbShowAgentTask);
		cb.setChecked(cfg.showAgentTask);		
	}
	
	@Override
	public void save() {
		super.save();

		CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();

		CheckBox cb;		
		cb = (CheckBox)findViewById(R.id.cbHideRest);
		cfg.hideRestQTY = cb.isChecked();
		
		cb = (CheckBox)findViewById(R.id.cbShowAgentTask);
		cfg.showAgentTask = cb.isChecked();		
		
		ConfigManager.save();
	}
}
