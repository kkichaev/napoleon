/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Настройки
 *
 * kki   10/10/2010   creating
 */
package com.grsoft.napoleon;


import static com.grsoft.util.Debug.dbgPrint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.SettingActivity;

public class Configuration extends SettingActivity{
	
	protected int getLayoutID() { return  R.layout.config; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());
		init();
	}
	
	protected void init()
	{
		EditText edIp = (EditText) findViewById(R.id.edIp);
		EditText edIp2 = (EditText) findViewById(R.id.edIp2);
		EditText edPort = (EditText) findViewById(R.id.edPort);
		EditText edLogin = (EditText) findViewById(R.id.edLogin);
		EditText edPassw = (EditText) findViewById(R.id.edPassw);
		
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		edIp.setText(config.address);
		edIp2.setText(config.address2);
		edPort.setText(Integer.toString(config.port));
		edLogin.setText(getLogin(config));
		edPassw.setText(getPassw(config));
		
		if(Features.HIDE_PASSWORD)
			edPassw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		if(Features.HIDE_LOGIN)
			edLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		if(Features.ORDER_ONLINE) {
			View v = findViewById(R.id.trOnLine);
			if( v != null ) {
				v.setVisibility(View.VISIBLE);
				Spinner sp = (Spinner)findViewById(R.id.spOnLine);
				sp.setSelection(((com.grsoft.napoleon.util.CfgNplW)config).onLineIP);
			}
		}
	}

	protected String getPassw(Config config) {
		return config.passw;
	}

	protected String getLogin(Config config) {
		return config.login;
	}
	
	protected void setLogin(Config config, String login){
		config.login = login;
	}
	
	protected void setPassword(Config config, String passw){
		config.passw = passw;
	}

	@Override
	public void save() {
		EditText edIp = (EditText) findViewById(R.id.edIp);
		EditText edIp2 = (EditText) findViewById(R.id.edIp2);
		EditText edPort = (EditText) findViewById(R.id.edPort);
		EditText edLogin = (EditText) findViewById(R.id.edLogin);
		EditText edPassw = (EditText) findViewById(R.id.edPassw);
		
		try
		{
			int port = Integer.parseInt(edPort.getText().toString());
			Config config = ConfigManager.getConfig();
			
			config.address = edIp.getText().toString().trim();
			config.address2 = edIp2.getText().toString().trim();
			config.port = port;
			config.port2 = port;
			setLogin(config, edLogin.getText().toString().trim());
			setPassword(config, edPassw.getText().toString().trim());
						
			if(Features.ORDER_ONLINE) {
				Spinner sp = (Spinner)findViewById(R.id.spOnLine);
				if( sp != null ) {
					((com.grsoft.napoleon.util.CfgNplW)config).onLineIP = sp.getSelectedItemPosition();
				}
			}
			ConfigManager.save();
		}
		catch(Exception exception)
		{
			dbgPrint(exception.getMessage());
		}
		
	}

	@Override
	public void update() {
		init();
		
	}

	@Override
	public int getName() {
		return R.string.network;
	}

	@Override
	public int getIcon() {
		return R.drawable.setting_network;
	}
	
	@Override
	public boolean isAdminSettings() {
		return true;
	}
}
