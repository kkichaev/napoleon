package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText edLogin;
	private EditText edPassword;
	private CfgNpl cfg;
	
	public static void open(Context c) {
//		Intent i = new Intent(c, LoginActivity.class);
//		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		edLogin = (EditText)findViewById(R.id.edLogin);
		edPassword = ((EditText)findViewById(R.id.edPassword));		
		
		cfg = (CfgNpl) ConfigManager.getConfig();
		edLogin.setText(cfg.login);
		edPassword.setText(cfg.passw);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				checkLogin();
			}
		});
	}
	
	protected void checkLogin() {
		AgentRcv a = new AgentRcv();
		String table = DataObjectInfo.getInstance().getTableName(AgentRcv.class);
		DbReader r = new DbReader();
		String login = edLogin.getText().toString().trim();
		String pwd = edPassword.getText().toString().trim();
		
		boolean bdo = r.select(a, table, null);
		while( bdo ) {
			if( a.login.equals(login) && a.password.equals(pwd) ) {
				cfg.login = login;
				cfg.passw = pwd;
				ConfigManager.save();
				finish();
				break;
			}
			bdo = r.selectNext(a);
		}
		r.close();
		if( !bdo )
			Toast.makeText(this, "Не правильный логин/пароль. Повторите, пожалуйста, ввод",	Toast.LENGTH_LONG).show();			
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
//			Toast.makeText(this, R.string.ask_to_exit,	Toast.LENGTH_LONG).show();			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
