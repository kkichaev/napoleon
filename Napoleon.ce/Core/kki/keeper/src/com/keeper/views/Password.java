package com.keeper.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.keeper.KeeperApp;
import com.keeper.R;
import com.keeper.utils.Crypto;

public class Password extends Activity {
	private static final String D_TAG = "Password";
	private static final int SET_PASSWORD_DLG = 0;
	EditText edPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		Button btnGo = (Button) findViewById(R.id.btnGo);
		edPassword = (EditText) findViewById(R.id.edPassword);
		
		btnGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(passwordCheck()){
					Group.open(Password.this);
				}
				else
					Toast.makeText(Password.this, 
							R.string.invalid_password, 
							Toast.LENGTH_LONG).show();
			}

			private boolean passwordCheck() {
				
				String password = edPassword.getText().toString();
				SQLiteDatabase db = KeeperApp.dbManager.getWritableDatabase();
				Cursor cursor = db.query("config", new String[]{"value"}, "key=?", 
						new String[]{"password"}, null, null, null);
				
				String decriptPassword = "";
				String passwordValue = ""; 
					
				try{
					if(!cursor.moveToFirst())
						return false;
				
					passwordValue = cursor.getString(cursor.getColumnIndex("value"));
					decriptPassword = Crypto.decrypt(password, passwordValue);
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}finally{
					cursor.close();
				}
				
				Log.d(D_TAG, "inputPassword: " + password);
				Log.d(D_TAG, "decriptPassword: " + decriptPassword);
				Log.d(D_TAG, "paswordFromBase: " + passwordValue);
				
				if(decriptPassword.equals(edPassword.getText().toString())){
					KeeperApp.masterPassword = password;
					return true;
				}else
					return false;
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.password_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itSetPassword:
			showDialog(SET_PASSWORD_DLG);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id)
		{
		case SET_PASSWORD_DLG:
			final EditText edInput = (EditText)dialog.findViewById(R.id.edInput);
			edInput.setText("");
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE, 
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new ChangeMasterPassword(Password.this).execute(edInput.getText().toString());
							
						}
					});
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case SET_PASSWORD_DLG:
			return createSetPasswordDlg();
		default:
			return super.onCreateDialog(id);
		}
		
	}

	private Dialog createSetPasswordDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.inputdlg, null);
		builder.setView(view);
		builder.setTitle(R.string.change_password_title);
		builder.setMessage(R.string.ask_to_input_new_password);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		edPassword.setText("");
	}
}

class ChangeMasterPassword extends AsyncTask<String, Void, Boolean>{

	private Context context;

	public ChangeMasterPassword(Context context){
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try{
			SQLiteDatabase db = KeeperApp.dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("value", Crypto.encrypt(arg0[0], arg0[0]));
		if (db.update("config", cv, "key=?", new String[]{"password"}) != -1){
			KeeperApp.dbManager.clearTables();
			return true;
		}else 
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (!result)
			Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
	}
	
}
