package com.ksoft.ftpwriter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {
	private static final String PREF = "pref";
	private static final String FILE = "file";
	private static final String SERVER = "server";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String PATH = "path";
	private static final String TEXT = "text";
	
	private View btnSend;
	private EditText edFile;
	private EditText edServer;
	private EditText edUser;
	private EditText edPassword;
	private EditText edPath;
	private EditText edText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		inflateView();
		initView();
	}

	private void initView() {
		btnSend.setOnClickListener(onSend());
	}

	private void inflateView() {
		edFile = (EditText) findViewById(R.id.edFile);
		edServer = (EditText) findViewById(R.id.edServer);
		edUser = (EditText) findViewById(R.id.edUser);
		edPassword = (EditText) findViewById(R.id.edPassword);
		edPath = (EditText) findViewById(R.id.edPath);
		edText = (EditText) findViewById(R.id.edText);
		btnSend = findViewById(R.id.btnSend);
	}

	private OnClickListener onSend() {
		return new OnClickListener() {
			@Override public void onClick(View v) {
				new FtpSender(){
					protected void onPreExecute() { btnSend.setEnabled(false); };
					protected void onPostExecute(Boolean result) { 
						btnSend.setEnabled(true); 
						Toast.makeText(getContext(), result ? R.string.success : R.string.error, Toast.LENGTH_SHORT).show();
					};
				}.execute(getParams());
			}
		};
	}
	
	private Context getContext(){ return this; }

	protected Object getParams() {
		FtpData d = new FtpData();
		d.file = edFile.getText().toString().trim();
		d.server = edServer.getText().toString().trim();
		d.user = edUser.getText().toString().trim();
		d.password = edUser.getText().toString().trim();
		d.path = edPath.getText().toString().trim();
		d.text = edText.getText().toString().trim();
		
		return d;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
		edFile.setText(p.getString(FILE, ""));
		edServer.setText(p.getString(SERVER, ""));
		edUser.setText(p.getString(USER, ""));
		edPassword.setText(p.getString(PASSWORD, ""));
		edPath.setText(p.getString(PATH, ""));
		edText.setText(p.getString(TEXT, ""));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
		Editor e =  p.edit();
		e.putString(FILE, edFile.getText().toString().trim());
		e.putString(SERVER, edServer.getText().toString().trim());
		e.putString(USER, edUser.getText().toString().trim());
		e.putString(PASSWORD, edPassword.getText().toString().trim());
		e.putString(TEXT, edText.getText().toString().trim());
		e.putString(PATH, edPath.getText().toString());
		e.commit();
	}
}
