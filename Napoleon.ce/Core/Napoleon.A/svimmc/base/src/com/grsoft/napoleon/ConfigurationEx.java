package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.CfgNplEx;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ConfigurationEx extends Configuration {
	EditText edUnfireRest;
	
	@Override
	protected int getLayoutID() {
		return R.layout.configex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnSync).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateDBEx.openActivity(v.getContext());
			}
		});
	}
	
	@Override
	protected void init() {
		super.init();
		
		edUnfireRest = (EditText) findViewById(R.id.edUnfireRest);
		CfgNpl config = (CfgNpl) ConfigManager.getConfig();
		edUnfireRest.setText(Integer.toString(((CfgNplEx)config).unfire_rest));
	}
	
	@Override
	public void save() {
		try{
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			((CfgNplEx)config).unfire_rest = Integer.parseInt(edUnfireRest.getText().toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		super.save();
	}
}
