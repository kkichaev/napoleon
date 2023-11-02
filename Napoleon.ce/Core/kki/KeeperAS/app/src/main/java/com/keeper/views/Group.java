package com.keeper.views;

import java.util.Calendar;
import java.util.Comparator;

import com.keeper.KeeperApp;
import com.keeper.R;
import com.keeper.db.DataBaseManager;
import com.keeper.db.GroupAsyncOper;
import com.keeper.db.data.DataObject;
import com.keeper.utils.Crypto;
import com.keeper.utils.DeleteDialog;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Group extends BaseActivity 
	implements DataSetContext{
    public static final String TAG = "Group";
	private static final int DLG_ADD_GROUP = 0;
	private static final int DLG_DEL_GROUP = 1;
	private static final int DLG_EDIT_GROUP = 2;
	public static final int DLG_RECREATE_GROUP = 3;
	public static final String GROUP_ID_STR = "group_id";
	private static final String GROUP_NAME_STR = "group_name";
	private GroupAdapter groupAdapter;
	private Cursor adapterCursor;
	private ListView lvGroups;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group);
        Log.d(TAG, "onCreate");
        setTitle(R.string.group_title);
        lvGroups = (ListView)findViewById(R.id.lvGroups);
        lvGroups.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
					long arg3) {
				com.keeper.db.data.Group item = (com.keeper.db.data.Group) adapter.getItemAtPosition(pos);
				Uri.open(Group.this, item._id);
			}
		});
        
        registerForContextMenu(lvGroups);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	adapterCursor.close();
    }
  
    @Override
    protected void onResume() {
    	super.onResume();
    	SQLiteDatabase db = new DataBaseManager(getApplicationContext()).getWritableDatabase();
        adapterCursor = db.query("[group]", new String[]{"_id", "name"}, 
        		"deleted IS NULL", null, null, null, null);
        
        groupAdapter = new GroupAdapter(this, adapterCursor);
        lvGroups.setAdapter(groupAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.base_opt_mnu, menu); 
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId())
		{
		case R.id.itAdd:
			showDialog(DLG_ADD_GROUP);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id)
    	{
    	case DLG_ADD_GROUP:
    	case DLG_EDIT_GROUP:
    		Log.d(TAG, "onCreateDialog: DLG_ADD_GROUP/DLG_EDIT_GROUP");
    		return createOperationDialog(
    				getResources().getString(R.string.ask_to_get_group_name));
    	case DLG_DEL_GROUP:
    		Log.d(TAG, "onCreateDialog: DLG_DEL_GROUP");
			return DeleteDialog.create(this);
    	default:
    		return super.onCreateDialog(id);
    	}
    }
    
	@Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
    	
    	Log.d(TAG, "onPrepareDialog");
    	final EditText edInput = (EditText)dialog.findViewById(R.id.edInput);
    	
    	switch(id){
    	case DLG_ADD_GROUP:
        	edInput.setText("");
        	((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE, 
        			getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						String groupName = edInput.getText().toString(); 
						
						if (groupName.trim().length() == 0){
							Toast.makeText(Group.this, 
									getResources().getString(R.string.error_create_empty_group), 
									Toast.LENGTH_SHORT).show();
							return;
						}
						
						new InsertGroup(Group.this).execute(new Object[]{groupName});
					}catch(Exception e){
						Toast.makeText(Group.this, 
								getResources().getString(R.string.error), 
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			});
        	break;
    	case DLG_DEL_GROUP:
    		final long del_group_id = args.getLong(GROUP_ID_STR);
    		((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE, 
    				getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
    					
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						new DeleteGroup(Group.this).execute(del_group_id);
    					}
    				});
    		break;
    	case DLG_EDIT_GROUP:
    		final long edit_group_id = args.getLong(GROUP_ID_STR);
    		edInput.setText(args.getString(GROUP_NAME_STR));
    		edInput.selectAll();
    		((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE, 
    				getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try{
								String groupName = edInput.getText().toString(); 
								
								if (groupName.trim().length() == 0){
									Toast.makeText(Group.this, 
											getResources().getString(R.string.error_create_empty_group), 
											Toast.LENGTH_SHORT).show();
									return;
								}
								
								new EditGroup(Group.this).execute(new Object[]{edit_group_id,
										edInput});
							}catch(Exception e){
								Toast.makeText(Group.this, 
										getResources().getString(R.string.error), 
										Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}
							
						}
    		});
    		break;
    	}
    }
    
	@Override
    public void notifyAdapterDataChanged(){
		groupAdapter.reload();
		groupAdapter.notifyDataSetChanged();
    }
    
    public Dialog createOperationDialog(String caption){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.inputdlg, null);
		builder.setView(view);
		builder.setNegativeButton(getResources().getString(R.string.cancel), null);
		builder.setPositiveButton(getResources().getString(R.string.ok), null);
		builder.setMessage(caption);
		return builder.create();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	getMenuInflater().inflate(R.menu.base_oper_mnu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo(); 
    	View view = info.targetView;
    	int pod = info.position;
    	long id = ((com.keeper.db.data.Group) groupAdapter.getItem(pod))._id;
    	Bundle bundle = new Bundle();
    	
    	switch (item.getItemId())
		{
		case R.id.itAdd:
			showDialog(DLG_ADD_GROUP);
			return true;
		case R.id.itDelete:
			bundle.putLong(GROUP_ID_STR, id);
			showDialog(DLG_DEL_GROUP, bundle);
			return true;
		case R.id.itEdit:
			TextView tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
			bundle.putLong(GROUP_ID_STR, id);
			bundle.putString(GROUP_NAME_STR, tvGroupName.getText().toString());
			showDialog(DLG_EDIT_GROUP, bundle);
		default:
			return super.onContextItemSelected(item);
		}
    }

	public static void open(Context context) {
		Intent intent = new Intent(context, Group.class);
		context.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX > 0)
			moveTaskToBack(true);
		return true;
	}
}

class InsertGroup extends GroupAsyncOper{
	public final String D_TAG = "InsertGroup";
	
	public InsertGroup(Group main){
		super(main);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		try{
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("name", Crypto.encrypt((String)params[0]));
			cv.put("created", Calendar.getInstance().getTime().getTime());
			boolean result = false;
			if (db.insert("[group]", null, cv) != -1)
				result = true;
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

class DeleteGroup extends GroupAsyncOper{
	public DeleteGroup(Group main){
		super(main);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		try{
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues cv = new ContentValues();
			long group_id = (Long)params[0];
			cv.put("deleted", Calendar.getInstance().getTime().getTime());
			String[] args = new String[]{String.format("%d", group_id)}; 
			if (db.update("[group]", cv, "_id=?", args) != -1 &&
					db.update("uri", cv, "group_id=?", args) != -1 &&
					db.update("accounts", cv, "uri_id IN (SELECT _id FROM uri WHERE group_id=?)", args) != -1) 
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

class EditGroup extends GroupAsyncOper{
	public EditGroup(Group main) {
		super(main);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		try{
			long id = (Long)params[0];
			String groupName = ((EditText)params[1]).getText().toString();
			SQLiteDatabase db = new DataBaseManager(context.getApplicationContext()).getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("name", Crypto.encrypt(groupName));
			cv.put("modified", Calendar.getInstance().getTime().getTime());
			if (db.update("[group]", cv, "_id=?", new String[]{String.format("%d", id)}) != -1)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

class GroupAdapter extends DataAdapter<com.keeper.db.data.Group> implements Comparator<com.keeper.db.data.Group>{

	public GroupAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	View newView(Context context, DataObject item, ViewGroup parent) {
		return View.inflate(context, R.layout.group_list_row, null);
	}

	@Override
	void bindView(View view, Context context, DataObject item) {
		TextView tv = (TextView) view.findViewById(R.id.tvGroupName);
		tv.setText(((com.keeper.db.data.Group)item).name);
		
	}

	@Override
	com.keeper.db.data.Group createItem(Cursor c) {
		com.keeper.db.data.Group result = new com.keeper.db.data.Group();
		result._id = c.getLong(c.getColumnIndex("_id"));
		
		try {
			result.name = Crypto.decrypt(c.getString(c.getColumnIndex("name")));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	Comparator<? super com.keeper.db.data.Group> getCmp() {
		return this;
	}

	@Override
	public int compare(com.keeper.db.data.Group lhs, com.keeper.db.data.Group rhs) {
		return lhs.name.compareTo(rhs.name);
	}
}