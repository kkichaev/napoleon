package com.keeper.views;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.keeper.KeeperApp;
import com.keeper.R;
import com.keeper.db.DataBaseManager;
import com.keeper.db.GroupAsyncOper;
import com.keeper.utils.Crypto;
import com.keeper.utils.DeleteDialog;

public class Uri extends BaseActivity
	implements DataSetContext{
	private static final String TAG  = "Uri";
	private static final String GROUP_ID_STR = "group_id";
	private static final int DLG_ADD_URI = 0;
	private static final int DLG_DEL_URI = 1;
	private static final int DLG_EDIT_URI = 2;
	private static final int DLG_MOVE_URI = 3;
	private static final String URI_ID_STR = "uri_id";
	private static final String URI_NAME_STR = "uri_name";
	private static final String ALIAS_NAME_STR = "alias_name";
	
	private long group_id;
	private CursorAdapter adapter;
	private ListView lvUri;
	private final static int MOVE_URI_ID = 0;
	private GroupSimpleSelectAdapter groupSimpleSelectAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uri);
		
		if (savedInstanceState == null)
			group_id = getIntent().getExtras().getLong(GROUP_ID_STR, -1);
		else
			group_id = savedInstanceState.getLong(GROUP_ID_STR, -1);
		
		if(group_id == -1){
			Toast.makeText(this, R.string.error, Toast.LENGTH_LONG);
			return;
		}
		
		SQLiteDatabase db = new DataBaseManager(getApplicationContext()).getReadableDatabase();
		Cursor cursor = db.query("[group]", new String[]{"name"}, "_id=?", 
				new String[]{String.format("%d", group_id)}, null, null, null);
		
		try {
			if (!cursor.moveToFirst()){
				Toast.makeText(this, R.string.error, Toast.LENGTH_LONG);
				return;
			}
			
			setTitle(getResources().getString(R.string.url_title, 
					Crypto.decrypt(
							cursor.getString(cursor.getColumnIndex("name")))));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		lvUri = (ListView)findViewById(R.id.lvUri);
		registerForContextMenu(lvUri);
		lvUri.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "lvUri onItemClick");
				Integer id = (Integer)arg1.getTag();
				Accounts.open(Uri.this, id);
			}
		});
	}
	
	public static void open(Context context, long group_id){
		Intent intent = new Intent(context, Uri.class);
		intent.putExtra(GROUP_ID_STR, group_id);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base_opt_mnu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itAdd:
			showDialog(DLG_ADD_URI);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Dialog createNewUriDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.inputuridlg, null);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(GROUP_ID_STR, group_id);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_ADD_URI:
		case DLG_EDIT_URI:
			return createNewUriDlg();
		case DLG_DEL_URI:
			return DeleteDialog.create(this);
		case DLG_MOVE_URI:
			return createMoveDlg();
		default: 
			return super.onCreateDialog(id);
		}
	}

	private Dialog createMoveDlg() {
		Log.d(TAG, "createMoveDlg");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("");
		
        
		builder.setSingleChoiceItems(groupSimpleSelectAdapter, 0, null);
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void notifyAdapterDataChanged() {
		adapter.getCursor().requery();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SQLiteDatabase db = new DataBaseManager(getApplicationContext()).getReadableDatabase();
		Cursor cursor = db.query("uri", 
				new String[]{"_id", "uri", "alias"}, "deleted IS NULL AND group_id=?" , 
				new String[]{String.format("%d", group_id)}, null, null, "uri, alias");
		adapter = new UriAdapter(this, cursor);
		lvUri.setAdapter(adapter);
		
		groupSimpleSelectAdapter = new GroupSimpleSelectAdapter(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		adapter.getCursor().close();
		groupSimpleSelectAdapter.getCursor().close();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, final Bundle args) {
		final EditText edUri = (EditText)dialog.findViewById(R.id.edUri);
		final EditText edAlias = (EditText)dialog.findViewById(R.id.edAlias);
		
		switch(id){
		case DLG_ADD_URI:
			edUri.setText("");
			edAlias.setText("");
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new InsertUri(Uri.this).execute(edUri.getText().toString(),
									edAlias.getText().toString(), group_id);
							
						}
					});
			break;
		case DLG_DEL_URI:
			final int del_uri_id = args.getInt(URI_ID_STR);
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new DeleteUri(Uri.this).execute(del_uri_id);
							
						}
					});
			break;
		case DLG_EDIT_URI:
			edUri.setText(args.getString(URI_NAME_STR));
			edAlias.setText(args.getString(ALIAS_NAME_STR));
			final int edit_uri_id = args.getInt(URI_ID_STR);
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new EditUri(Uri.this).execute(edUri.getText().toString(),
									edAlias.getText().toString(), edit_uri_id);
							
						}
					});
			break;
		case DLG_MOVE_URI:
			Cursor c = new DataBaseManager(getApplicationContext()).getReadableDatabase().query("[group]",
					new String[]{"name"}, "_id=?", 
					new String[]{String.format("%d",group_id)}, null, null, null);
			
			if (c.moveToFirst()){
				try{
					String name = Crypto.decrypt(c.getString(c.getColumnIndex("name")));
					int pos = 0;
					for (pos = 0; pos < groupSimpleSelectAdapter.getCount(); pos++){
						Cursor ci = (Cursor) groupSimpleSelectAdapter.getItem(pos);
						String itemName = Crypto.decrypt(ci.getString(ci.getColumnIndex("name")));
						if (itemName.equals(name))
							break;
					}
					
					((AlertDialog)dialog).getListView().setItemChecked(pos, true);
					
					((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE,
							getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									int pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
									Cursor c = (Cursor) groupSimpleSelectAdapter.getItem(pos);
									int new_group_id = c.getInt(c.getColumnIndex("_id"));
									
									if(new_group_id != group_id)
										new MoveUri(Uri.this).
											execute(new_group_id, args.getInt(URI_ID_STR));
								}
							});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			c.close();
			break;
		}
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	getMenuInflater().inflate(R.menu.base_oper_mnu, menu);
    	menu.add(0, MOVE_URI_ID , 0, R.string.move);
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		View view = ((AdapterContextMenuInfo)item.getMenuInfo()).targetView;
		int id = (Integer)view.getTag();
    	Log.d(TAG, "onContextItemSelected id = :" + Integer.toString(id));
    	Bundle bundle = new Bundle();
    	
		switch(item.getItemId()){
		case R.id.itAdd:
			showDialog(DLG_ADD_URI);
			return true;
		case R.id.itDelete:
			bundle.putInt(URI_ID_STR, id);
			showDialog(DLG_DEL_URI, bundle);
			return true;
		case R.id.itEdit:
			bundle.putInt(URI_ID_STR, id);
			TextView tvUriName = (TextView)view.findViewById(R.id.tvUriName);
			TextView tvAlias = (TextView)view.findViewById(R.id.tvAlias);
			bundle.putString(URI_NAME_STR, tvUriName.getText().toString());
			bundle.putString(ALIAS_NAME_STR, tvAlias.getText().toString());
			showDialog(DLG_EDIT_URI, bundle);
			return true;
		case MOVE_URI_ID:
			bundle.putInt(URI_ID_STR, id);
			showDialog(DLG_MOVE_URI, bundle);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}

class UriAdapter extends CursorAdapter{

	public UriAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String uri_str = "";
		String alias_str = "";
		
		try{
			uri_str = Crypto.decrypt(cursor.getString(cursor.getColumnIndex("uri")));
			alias_str = Crypto.decrypt(cursor.getString(cursor.getColumnIndex("alias")));
		} catch(Exception e){
			e.printStackTrace();
		}
		
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		view.setTag(id);
		TextView tvUriName = (TextView) view.findViewById(R.id.tvUriName);
		TextView tvAlias = (TextView) view.findViewById(R.id.tvAlias);
		
		tvUriName.setText(uri_str);
		tvAlias.setText(alias_str);
		
		if (alias_str.length() == 0){ 
			tvAlias.setVisibility(View.GONE);
			tvUriName.setVisibility(View.VISIBLE);
		}
		else{
			tvUriName.setVisibility(View.GONE);
			tvAlias.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.uri_list_row, null);
	}
	
}

class InsertUri extends GroupAsyncOper{

	public InsertUri(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		try{
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("uri", Crypto.encrypt((String)params[0]));
			values.put("alias", Crypto.encrypt((String)params[1]));
			values.put("created", Calendar.getInstance().getTime().getTime());
			values.put("group_id", (Long)params[2]);
			
			if (db.insert("uri", null, values) != -1)
				return true;
			else
				return false;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
}

class DeleteUri extends GroupAsyncOper{

	public DeleteUri(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		try{
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("deleted", Calendar.getInstance().getTime().getTime());
			int uri_id = (Integer)params[0];
			
			if (db.update("uri", values, "_id=?", 
					new String[]{Integer.toString(uri_id)}) != -1)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

class MoveUri extends GroupAsyncOper{
	private static final String TAG = "MoveUri";
	
	public MoveUri(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... arg0) {
		try{
			Long group_id = (Long)arg0[0];
			int uri_id = (Integer)arg0[1];
			
			Log.d(TAG, "doInBackground args count: " + Integer.toString(arg0.length));
			Log.d(TAG, "group_id = " + Long.toString(group_id));
			Log.d(TAG, "uri_id = " + Integer.toString(uri_id));
			
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("group_id", group_id);
			if (db.update("uri", values, "_id=?", new String[]{Integer.toString(uri_id)}) != -1)
				return true;
			return false;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}
class EditUri extends GroupAsyncOper{
	private static final String TAG = "EditUri";
	
	public EditUri(Context context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(Object... arg0) {
		try{
			Log.d(TAG, "doInBackground args count: " + Integer.toString(arg0.length));
			String uri = (String)arg0[0];
			String alias = (String)arg0[1];
			int id = (Integer)arg0[2];
			Log.d(TAG, "uri: " + uri + " alias: " + alias + " id: " + Integer.toString(id));
			ContentValues values = new ContentValues();
			values.put("uri", Crypto.encrypt(uri));
			values.put("alias", Crypto.encrypt(alias));
			values.put("modified", Calendar.getInstance().getTime().getTime());
			
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			if (db.update("uri", values, "_id=?", new String[]{Integer.toString(id)}) != -1)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}

class GroupSimpleSelectAdapter extends CursorAdapter{
	public GroupSimpleSelectAdapter(Context context) {
		super(context,
				new DataBaseManager(context.getApplicationContext()).getWritableDatabase().query(
						"[group]", new String[]{"_id", "name"}, 
						"deleted IS NULL", null, null, null, "name"), true);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String groupName = "";
		
		try{
			groupName = Crypto.decrypt(cursor.getString(cursor.getColumnIndex("name")));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		view.setTag(id);
		((CheckedTextView)view).setText(groupName);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.group_select_row, null);
	}
}