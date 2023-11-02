package com.keeper.views;

import java.util.Calendar;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.keeper.KeeperApp;
import com.keeper.R;
import com.keeper.db.GroupAsyncOper;
import com.keeper.utils.Crypto;
import com.keeper.utils.DeleteDialog;

public class Accounts extends BaseActivity 
	implements DataSetContext{
	private static final String URI_ID_STR = "uri_id_str";
	private static final String ACCOUNT_ID_STR = "account_id_str";
	private static final int DLG_ADD_ACCOUNT = 0;
	private static final int DLG_DEL_ACCOUNT = 1;
	private static final int DLG_EDIT_ACCOUNT = 3;
	private static final String LOGIN_VALUE_STR = "login_value_str";
	private static final String PASSWORD_VALUE_STR = "password_value_str";
	private int uri_id;
	public static String TAG = "Accounts";
	public AccountsAdapter adapter;
	public ListView lvAccounts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);
		
		Intent intent = getIntent();
		uri_id = intent.getExtras().getInt(URI_ID_STR);
		
		SQLiteDatabase db = KeeperApp.dbManager.getReadableDatabase();
		Cursor cursor = db.query("uri", new String[]{"uri"}, "_id=?", 
				new String[]{Integer.toString(uri_id)}, null, null, null);
		
		try{
			if (cursor.moveToFirst())
				setTitle(Crypto.decrypt(cursor.getString(cursor.getColumnIndex("uri"))));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cursor.close();
		}
		
		lvAccounts = (ListView) findViewById(R.id.lvAccounts);
		lvAccounts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "lvAccounts.onItemClick arg0: " + arg0.toString() + 
						" arg1: " + arg1.toString());
				Log.d(TAG, "lvAccounts.onItemClick arg2: " + Integer.toString(arg2) +
						" arg3: " + Long.toString(arg3));
			}
		});
		
		registerForContextMenu(lvAccounts);
	}
	
	public static void open(Context context,int uri_id){
		Intent intent = new Intent(context, Accounts.class);
		intent.putExtra(URI_ID_STR, uri_id);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.base_oper_mnu, menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SQLiteDatabase db = KeeperApp.dbManager.getReadableDatabase();
		Cursor cursor = db.query("accounts", 
				new String[] {"_id", "login", "passw"},  "uri_id=? and deleted IS NULL", 
				new String[]{Integer.toString(uri_id)}, null, null, "login");
		adapter = new AccountsAdapter(this, cursor);
		lvAccounts.setAdapter(adapter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		adapter.getCursor().close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base_opt_mnu, menu);
		return true;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Bundle bundle = new Bundle();
		View view = ((AdapterContextMenuInfo)item.getMenuInfo()).targetView;
		int id = (Integer)view.getTag();
		
		switch(item.getItemId()){
		case R.id.itAdd:
			showDialog(DLG_ADD_ACCOUNT);
			return true;
		case R.id.itDelete:
			bundle.putInt(ACCOUNT_ID_STR, id);
			showDialog(DLG_DEL_ACCOUNT, bundle);
			return true;
		case R.id.itEdit:
			TextView tvLogin = (TextView) view.findViewById(R.id.tvLogin);
			TextView tvPassw = (TextView) view.findViewById(R.id.tvPassword);
			bundle.putInt(ACCOUNT_ID_STR, id);
			bundle.putString(LOGIN_VALUE_STR, tvLogin.getText().toString());
			bundle.putString(PASSWORD_VALUE_STR, (String)tvPassw.getTag());
			showDialog(DLG_EDIT_ACCOUNT, bundle);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itAdd:
			showDialog(DLG_ADD_ACCOUNT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_ADD_ACCOUNT:
		case DLG_EDIT_ACCOUNT:
			return createNewAccountDialog();
		case DLG_DEL_ACCOUNT:
			return DeleteDialog.create(this);
		default:
			return super.onCreateDialog(id);
		}
	}
	private Dialog createNewAccountDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.inputaccount, null);
		builder.setView(view);
		Button btnGenPassw = (Button) view.findViewById(R.id.btnGenPassw);
		final EditText edPassw = (EditText) view.findViewById(R.id.edPassw);
		edPassw.setTransformationMethod(new PasswordTransformationMethod());
		btnGenPassw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new PasswordGen(edPassw).execute();
			}
		});
		
		CheckBox cbShowPassw = (CheckBox) view.findViewById(R.id.cbShowPassw);
		cbShowPassw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				edPassw.setTransformationMethod(((CheckBox)v).isChecked() ? 
						null : new PasswordTransformationMethod());
			}
		});
		
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		final EditText edLogin = (EditText) dialog.findViewById(R.id.edLogin);
		final EditText edPassw = (EditText) dialog.findViewById(R.id.edPassw);
		SharedPreferences pref = getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		CheckBox cbShowPassw = (CheckBox) dialog.findViewById(R.id.cbShowPassw);
		boolean showPassw = pref.getBoolean(Setting.SHOW_PASSW, true);
		
		if (cbShowPassw != null)
			cbShowPassw.setChecked(showPassw);
		
		edPassw.setTransformationMethod(showPassw ? null : new PasswordTransformationMethod());
		
		switch(id){
		case DLG_ADD_ACCOUNT:
			String login = pref.getString(Setting.LOGIN, "");
			edLogin.setText(login);
			edPassw.setText("");
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new InsertAccount(Accounts.this).execute(edLogin.getText().toString(),
									edPassw.getText().toString(), uri_id);
							
						}
					});
			break;
		case DLG_DEL_ACCOUNT:
			final int del_account_id = args.getInt(ACCOUNT_ID_STR);
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new DeleteAccount(Accounts.this).execute(del_account_id);
						}
					});
			break;
			
		case DLG_EDIT_ACCOUNT:
			final int edit_account_id = args.getInt(ACCOUNT_ID_STR);
			Log.d(TAG, "login: " + args.getString(LOGIN_VALUE_STR) + " passw: " +
					args.getString(PASSWORD_VALUE_STR) + " id: " + Integer.toString(edit_account_id));
			
			edLogin.setText(args.getString(LOGIN_VALUE_STR));
			edPassw.setText(args.getString(PASSWORD_VALUE_STR));
			
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new EditAccount(Accounts.this).execute(edLogin.getText().toString(),
									edPassw.getText().toString(), edit_account_id);
						}
					});
			break;
		}
		
	}

	@Override
	public void notifyAdapterDataChanged() {
		adapter.getCursor().requery();
	}
}

class AccountsAdapter extends CursorAdapter{

	public AccountsAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		try{
			String login = Crypto.decrypt(cursor.getString(cursor.getColumnIndex("login")));
			String passw = Crypto.decrypt(cursor.getString(cursor.getColumnIndex("passw")));
			
			TextView tvLogin = (TextView) view.findViewById(R.id.tvLogin);
			TextView tvPassw = (TextView) view.findViewById(R.id.tvPassword);
			
			tvLogin.setText(login);
			SharedPreferences pref = context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
					Context.MODE_PRIVATE);
			boolean showPassw = pref.getBoolean(Setting.SHOW_PASSW, true);
			tvPassw.setText(showPassw ? passw.toString() : 
				context.getResources().getString(R.string.password_string));
			
			tvPassw.setTag(passw.toString());
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			view.setTag(id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.accounts_list_row, null);
	}
}

class PasswordGen extends AsyncTask<Void, Void, String>{

	EditText target;
	
	public PasswordGen(EditText target) {
		this.target = target;
	}
	
	private final char[] passwordData = {'1','2','3','4','5', '6', '7', '8',
			'9','0','#','$','%','&','-','-','=','+',
			'q','Q','w','W','e','E','r','R','t','T','y','Y','u','U',
			'i','I','o','O','p','P','a','A','s','S','d','D','f','F',
			'g','G','h','H','j','J','k','K','l','L','z','Z','x','X',
			'c','C','v','V','b','B','n','N','m','M'};
	
	@Override
	protected String doInBackground(Void... params) {
		return makePassword();
	}
	
	private String makePassword(){
		final int dataLen = passwordData.length;
		
		try
		{
			SharedPreferences pref = target.getContext().
				getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
						Context.MODE_PRIVATE);
			
			int passwordLen = Integer.parseInt(
					pref.getString(Setting.PASSWORD_LEN, Setting.DEF_PASSWORD_LEN_VALUE));
			
			Random rnd = new Random();
			StringBuilder result = new StringBuilder();
			
			for(int i=0; i < passwordLen; i++)
				result.append(passwordData[rnd.nextInt(dataLen)]);
			
			return result.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		target.setText(result);
	}
}

class InsertAccount extends GroupAsyncOper {
	private static final String TAG = "InsertAccount";
	
	public InsertAccount(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... arg0) {
		try{
			String login = (String) arg0[0];
			String passw = (String) arg0[1];
			int uri_id = (Integer) arg0[2];
			
			Log.d(TAG, "insert login: " + login + " password: " + passw);
			SQLiteDatabase db = KeeperApp.dbManager.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("login", Crypto.encrypt(login));
			values.put("passw", Crypto.encrypt(passw));
			values.put("uri_id", uri_id);
			
			if (db.insert("accounts", null, values) != -1)
				return true;
			else
				return false;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

class DeleteAccount extends GroupAsyncOper{
	public DeleteAccount(Context context) {
		super(context);
	}

	@SuppressWarnings("unused")
	private static final String TAG = "DeleteAccount";

	@Override
	protected Boolean doInBackground(Object... arg0) {
		SQLiteDatabase db = KeeperApp.dbManager.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("deleted", Calendar.getInstance().getTime().getTime());
		int acc_id = (Integer)arg0[0];
		if (db.update("accounts", values, "_id=?", 
				new String[]{Integer.toString(acc_id)}) != -1)
			return true;
		else
			return false;
	}
}

class EditAccount extends GroupAsyncOper{

	public EditAccount(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... arg0) {
		try{
			String login = (String) arg0[0];
			String passw = (String) arg0[1];
			int acc_id = (Integer) arg0[2];
			
			SQLiteDatabase db = KeeperApp.dbManager.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("login", Crypto.encrypt(login));
			values.put("passw", Crypto.encrypt(passw));
			
			if (db.update("accounts", values, "_id=?", 
					new String[]{Integer.toString(acc_id)}) != -1)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}