package com.grsoft.napoleon;

import android.widget.EditText;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;


public class ConfigurationEx extends Configuration {
	EditText edIp3;
	
	@Override
	protected int getLayoutID() { return R.layout.configex;	}
	
	@Override
	protected void init() {
		super.init();
		CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
		
		edIp3 = (EditText) findViewById(R.id.edIp3);
		edIp3.setText(((CfgNplEx)config).address3);
	}
	
	@Override
	public void save() {
		EditText edPort = (EditText) findViewById(R.id.edPort);
		CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
		try{
			int port = Integer.parseInt(edPort.getText().toString());
			config.port3 = port;
			config.address3 = edIp3.getText().toString().trim();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		super.save();
	}
}
