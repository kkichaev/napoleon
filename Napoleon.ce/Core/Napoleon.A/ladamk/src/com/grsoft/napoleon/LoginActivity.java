package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.DataObjectInfo;

public class LoginActivity extends Activity {
	
	public static void open(Context c) {
		Intent i = new Intent(c, LoginActivity.class);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_page);
		
		
		Button btnOK = (Button) findViewById(R.id.btnOK); 
		btnOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				checkLogin();
			}
		});
		
//		-------------- CASE FOR DEBUG ONLY -------------------
		
//		btnOK.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				finish();
//				return true;
//			}
//		});
		
	}
	
	protected void checkLogin() {
		AgentRcv a = new AgentRcv();
		String table = DataObjectInfo.getInstance().getTableName(AgentRcv.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(a, table, "id = userid");
		r.close();

		String login = ((EditText)findViewById(R.id.edLogin)).getText().toString().trim();
		String password = ((EditText)findViewById(R.id.edPassword)).getText().toString().trim();
		if( bdo ) {
			if( a.login.equals(login) && a.password.equals(password) ) {
				finish();
			} else {
				Toast.makeText(this, "Не правильный логин/пароль. Повторите, пожалуйста, ввод",	Toast.LENGTH_LONG).show();						
			}
		} else {
			Toast.makeText(this, "Не найден текущий пользователь", Toast.LENGTH_LONG).show();
			finish();
		}
//		if( !bdo )
//			Toast.makeText(this, "Не правильный логин/пароль. Повторите, пожалуйста, ввод",	Toast.LENGTH_LONG).show();			
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
