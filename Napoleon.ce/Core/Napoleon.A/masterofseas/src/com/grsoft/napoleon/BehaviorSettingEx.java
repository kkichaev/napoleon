package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class BehaviorSettingEx extends BehaviorSetting {
	
	@Override protected int getContentViewID() { return R.layout.behavior_setting_ex; }
	
	@Override
	protected void init() {
		super.init();
		
		final CfgNplEx cfg = (CfgNplEx) config;
		((CheckBox)findViewById(R.id.cbStoreMode)).setChecked(cfg.simpleMode != 0);
		
		Spinner sp = (Spinner)findViewById(R.id.spOrg);
		DialogHelper.loadSpinnerFromDataObject(sp, OrgEx.class, new DialogHelper.Selected<OrgEx>() {
			@Override
			public boolean isSelected(OrgEx object) {
				return object.id.equals(cfg.simpleModeOrg);
			}
		}, true, "name");
	}
	
	@Override
	public void save() {
		CfgNplEx cfg = (CfgNplEx) config;
		Spinner sp = (Spinner)findViewById(R.id.spOrg);
		Org sel = (Org) sp.getSelectedItem();
		cfg.simpleModeOrg =(sel == null) ? "" : sel.id;
		
		cfg.simpleMode = ((CheckBox)findViewById(R.id.cbStoreMode)).isChecked() ? 1 : 0;
		if(cfg.simpleMode != 0 && cfg.simpleModeOrg.length() == 0) {
			cfg.simpleMode = 0;
			Toast.makeText(this, "Не выбран магазин для простого режима" , Toast.LENGTH_SHORT).show();
		}
		
		super.save();
	}
}
