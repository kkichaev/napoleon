package com.keeper.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.keeper.KeeperApp;
import com.keeper.R;
import com.keeper.db.DataBaseManager;
import com.keeper.utils.Crypto;

public class Password extends Activity {
	private static final String D_TAG = "Password";
	private static final int SET_PASSWORD_DLG = 0;
	EditText edPassword;
	private static final int PERMISSION_REQUEST = 0;
	private static final int REQUEST_SETTING_CODE = 100;


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
				SQLiteDatabase db = new DataBaseManager(getApplicationContext()).getWritableDatabase();
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
		if (id == SET_PASSWORD_DLG)
			return createSetPasswordDlg();
		else if (id == R.id.permission_not_set_dialog)
			return createPermissionNotSetDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createPermissionNotSetDlg() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Необходимо установить разрешения");
		ab.setMessage("В настройках установите все разрешения для программы!");
		ab.setCancelable(false);
		ab.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				android.net.Uri uri = android.net.Uri.fromParts("package",Password.this.getPackageName(), null);
				intent.setData(uri);
				startActivityForResult(intent, REQUEST_SETTING_CODE);
			}
		});
		return ab.create();
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
		checkApplicationPermission();
	}

	private void checkApplicationPermission(){
		if(Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
						PERMISSION_REQUEST);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
		if(rc == PERMISSION_REQUEST) {
			for(int i = 0; i < result.length; i++)
				if (result[i] != PackageManager.PERMISSION_GRANTED) {
					showDialog(R.id.permission_not_set_dialog);
					break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SETTING_CODE)
			checkApplicationPermission();
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
			DataBaseManager dbm = new DataBaseManager(context.getApplicationContext());
			SQLiteDatabase db = dbm.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("value", Crypto.encrypt(arg0[0], arg0[0]));

			if (db.update("config", cv, "key=?", new String[]{"password"}) != -1){
				dbm.clearTables();
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
