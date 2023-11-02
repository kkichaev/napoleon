package com.grsoft.napoleon;

import android.widget.Adapter;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Spinner sp = (Spinner)findViewById(R.id.spMonthRecreate);
		if( sp != null ) {
			Adapter vda = sp.getAdapter();
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			if (vda != null) {
				String checkStr = Integer.toString(config.daysToRecreate);
				for(int i = 0; i < vda.getCount(); i ++){
					if(vda.getItem(i).toString().equals(checkStr)) {
						sp.setSelection(i,  true);
						break;
					}
				}
			}
		}
	}
	
	protected void saveSettings() {
		Spinner sp = (Spinner)findViewById(R.id.spMonthRecreate);
		if( sp != null ) {
			CfgNpl c = (CfgNpl) ConfigManager.getConfig();
			c.daysToRecreate = Integer.parseInt((String)sp.getSelectedItem());
			ConfigManager.save();
		}
	}
}
