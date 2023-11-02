package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PotenzialOrg extends BaseActivity implements LocationListener {
	public static Class<? extends Activity> activity = PotenzialOrg.class;
	protected static final int WAIT_GPS_DLG_ID = 0;
	protected static final int INIT = 0;
	protected static final int REMOVE_GPS_DLG_ID = 1;
	protected static final int GPS_POS_RECIEVED = 2;
	protected Location location;
	private SecondTimer secondTimer;
    protected LocationManager locationManager;
    private List<Contact> contacts = new ArrayList<Contact>();
    private ContactAdapter contactAdapter;
    protected OrgImpl orgImpl = new OrgImpl();
    
    protected EditText edName;
    protected EditText edAddress;
    private TextView tvCoord;
    private ListView lvContacts;
    protected Button btnGetLocation;
    protected View btnOk;
    
    protected boolean appendMode = false;
    protected boolean editMode = false;

	public static final int MIN_TIME = 60000;
	public static final int MIN_DISTANCE = 10;
    
    public static void open(Context context){
    	Intent intent = new Intent(context, activity); 
    	intent.putExtra(APPEND_STR, true);
		context.startActivity(intent);
    }
	
    public static void open(Context context, long rowid){
    	open(context, rowid, false);
    }
    
    protected static final String EDIATBLE_STR = "editable";
    protected static final String APPEND_STR = "append";
    
    public static void open(Context context, long rowid, boolean editable){
    	Intent intent = new Intent(context, activity);
    	intent.putExtra(ExtrasConst.ORG_ID_STR, rowid);
    	intent.putExtra(EDIATBLE_STR, editable);
		context.startActivity(intent);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
		
		locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		
		lvContacts = ((ListView)findViewById(R.id.lvContacts));
		contactAdapter = getAdapter();
		lvContacts.setAdapter(contactAdapter);

		long rowid = getIntent().getLongExtra(ExtrasConst.ORG_ID_STR, ExtrasConst.INVALID_ID);

		edName = (EditText) findViewById(R.id.edName);
		
		edAddress = (EditText) findViewById(R.id.edAddress);
		tvCoord = (TextView) findViewById(R.id.tvCoord);
		
		if (rowid != ExtrasConst.INVALID_ID){
			if (orgImpl.read(rowid))
				for(Contact c : orgImpl.getData().contacts)
					contacts.add(c);
			
			orgImpl.close();
			
			Org org = orgImpl.getData();
			edName.setText(org.name);
			edAddress.setText(org.address);
			
			if ((org.latitude != 0 || org.longitude != 0) && tvCoord != null)
				tvCoord.setText(String.format(getString(R.string.shortlatitude) + ": %.5f, " 
						+getString(R.string.shortlongitude) + ": %.5f",
					((float)org.latitude) / Consts.GPS_SCALE, 
					((float)org.longitude) / Consts.GPS_SCALE));
		}
		
		editMode = getIntent().getExtras() == null ? true 
				:getIntent().getExtras().getBoolean(EDIATBLE_STR);
		appendMode = getIntent().getExtras() == null ? false 
				:getIntent().getExtras().getBoolean(APPEND_STR);
		
		btnOk = findViewById(R.id.btnOK);
		View btnCancel = findViewById(R.id.btnCancel);
		btnGetLocation = findViewById(R.id.btnGetLocation);
		ImageButton btnNewContact = findViewById(R.id.btnNewContact);

		if (appendMode || editMode){
			registerForContextMenu(lvContacts);
			btnOk.setOnClickListener(createOKListener());
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) { cancelEdit();  }
			});

			if (btnGetLocation != null)
				btnGetLocation.setOnClickListener(new GetCoordListener());

			if (btnNewContact != null)
				btnNewContact.setOnClickListener(new NewContactListener());
		}else{
			edName.setEnabled(false);
			edName.setFocusable(false);
			edAddress.setEnabled(false);
			edAddress.setFocusable(false);
			btnOk.setEnabled(false);
			btnCancel.setEnabled(false);
			btnGetLocation.setEnabled(false);
			btnNewContact.setEnabled(false);
		}
		
		lvContacts.setOnItemClickListener(new MakePhoneCall());

		if (orgImpl.getData().latitude != 0 && orgImpl.getData().longitude != 0) {
			location = new Location(LocationManager.GPS_PROVIDER);
			location.setLongitude((double)orgImpl.getData().longitude / Consts.GPS_SCALE);
			location.setLatitude((double)orgImpl.getData().latitude / Consts.GPS_SCALE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.potenzialorg_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itMap) {
			Intent intent = new Intent(this, PotenzialMap.class);
			intent.putExtra(PotenzialMap.LOCATION, location);
			startActivityForResult(intent, R.id.open_map);
			return true;
		} else if (item.getItemId() == R.id.itNewContact){
			contactDlgOpen(this);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	protected ContactAdapter getAdapter() {
		return new ContactAdapter();
	}

	protected int getContentViewId() {
		return R.layout.potenzial_org;
	}

	protected OKListener createOKListener() {
		return new OKListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		contactAdapter.notifyDataSetChanged();

		try {
			if (location == null) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (secondTimer != null){
			secondTimer.cancel();
			secondTimer = null;
		}

		locationManager.removeUpdates(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == R.id.open_map && resultCode == Activity.RESULT_OK){
			location = data.getParcelableExtra(PotenzialMap.LOCATION);
		}
	}

	public String genOrgId(){
		char postfix = 9;
		return Long.toString(Util.getDateTime().getTime()) + postfix;
	}

	class OKListener implements OnClickListener{
		
		protected String genOrgId(){
			return PotenzialOrg.this.genOrgId();
		}
		
		@Override
		public void onClick(View v) {
			
			if (checkExitCondition()){
				
				Org org = orgImpl.getData();
				
				if (appendMode){
					org.created = Util.getDate();
					org.id = genOrgId();
				}
				 
				org.name =edName.getText().toString();
				org.srchName = org.name.toUpperCase();
				org.address = edAddress.getText().toString();
				
				if(contacts != null && contacts.size() > 0)
				{
					Class <? extends DataObject> itemClass = 
							DataObjectInfo.getInstance().getListType(org.getClass(), "contacts");
					
					for(Contact c : contacts)
						try {
							Contact item = (Contact) itemClass.newInstance();
							item.name = c.name;
							item.phone = c.phone;
							org.contacts.add(item);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
					
				org.flags |= Org.FL_USER_CREATED;
				org.flags &= ~Org.FL_EXPORTED;
				
				postOnClick(org);
				
				if (location != null){
					org.latitude = (int) (location.getLatitude() * Consts.GPS_SCALE);
					org.longitude = (int) (location.getLongitude() * Consts.GPS_SCALE);
				}
				
				orgImpl.write();
				orgImpl.close();
				
				finish();
			}else
				Toast.makeText(v.getContext(), 
						R.string.ask_to_valid_data, 
						Toast.LENGTH_LONG).show();
		}
		
		protected void postOnClick(Org org){
		}
		
	}
	
	public boolean checkExitCondition(){
		 return edName.getText().toString().trim().length() > 0;
	}
	
	protected void cancelEdit() { finish(); }
	
	class GetCoordListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			showDialog(WAIT_GPS_DLG_ID);
			location = null;
			secondTimer = new SecondTimer();
			secondTimer.setHandler(handler);
			handler.sendEmptyMessage(INIT);
		}
		
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INIT:
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
						0, 0, PotenzialOrg.this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
						0, 0, PotenzialOrg.this);
				break;
			case REMOVE_GPS_DLG_ID:
				secondTimer = null;
				locationManager.removeUpdates(PotenzialOrg.this);
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(PotenzialOrg.this, R.string.impossible_get_gps, Toast.LENGTH_LONG).show();
				break;

			case GPS_POS_RECIEVED:
				secondTimer = null;
				locationManager.removeUpdates(PotenzialOrg.this);
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(PotenzialOrg.this, R.string.gps_recieved_succs, Toast.LENGTH_LONG).show();
				tvCoord.setText( String.format(getString(R.string.shortlatitude) + ": %f, " + getString(R.string.shortlongitude) + ": %f",
						location.getLatitude(), location.getLongitude()));
				break;  
			}
		};
	};
	
	protected android.app.Dialog onCreateDialog(int id) {
		Dialog result = null;
		
		switch(id){
		case WAIT_GPS_DLG_ID:
			result = ProgressDialog.show(this, "", getString(R.string.wait_for_current_location));
			result.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Log.d(Consts.D_TAG, "ProgressDialog.onKey: keyCode=" + Integer.toString(keyCode));
				
				if (keyCode == KeyEvent.KEYCODE_BACK){
					if (secondTimer != null)
						secondTimer.cancel();
					
					removeDialog(WAIT_GPS_DLG_ID);
				}
				
				return true;
				}
			});
		}
		return result;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;

		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	public class SecondTimer extends Timer{
		private final int DELAY_TIME = 1000;//1 sek;
		private final int WAIT_TIME = ((CfgNplW)ConfigManager.getConfig())
				.waitGpsCoordOnRequest;
		private WGSecondTask task = new WGSecondTask();
		private Handler handler;
		private int couner = 0; 
		
		public SecondTimer(){
			scheduleAtFixedRate(task, DELAY_TIME, DELAY_TIME);
		}
		
		public void setHandler(Handler handler){
			this.handler = handler;
		}
		
		class WGSecondTask extends TimerTask{

			@Override
			public void run() {
				Log.d(Consts.D_TAG, "WGTimerTask.run: " + couner++);
				
				if (location != null){
					handler.sendEmptyMessage(GPS_POS_RECIEVED);
					cancel();
				} else if (couner >= WAIT_TIME){
					handler.sendEmptyMessage(REMOVE_GPS_DLG_ID);
					cancel();
				}
			}
		}
	}
	
	class NewContactListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			contactDlgOpen(v.getContext());
		}
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		getMenuInflater().inflate(R.menu.simple_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo menuInfo = 
				(AdapterContextMenuInfo) item.getMenuInfo();
		
		if (item.getItemId() == R.id.itDelete){
			contacts.remove(menuInfo.position);
			contactAdapter.notifyDataSetChanged();
		}else if(item.getItemId() == R.id.itEdit){
			contactDlgOpen(PotenzialOrg.this, contacts.get(menuInfo.position));
		}else if(item.getItemId() == R.id.itAdd){
			contactDlgOpen(PotenzialOrg.this);
		}
		
		return true;
	}
	
	protected void contactDlgOpen(Context context) {
		contactDlgOpen(context, null);
	}
	
	private void contactDlgOpen(Context context, Contact contact){
		final Contact editContact = contact;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(editContact == null ? R.string.new_contact : R.string.edit);
		View view = getLayoutInflater().inflate(R.layout.contacts, null);
		builder.setView(view);
		if (editContact != null){
			 ((EditText)view.findViewById(R.id.edName)).setText(editContact.name);
			 ((EditText)view.findViewById(R.id.edPhone)).setText(editContact.phone);
		}
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = ((EditText)((AlertDialog)dialog).findViewById(R.id.edName)).getText().toString();
				String phone = ((EditText)((AlertDialog)dialog).findViewById(R.id.edPhone)).getText().toString();
				
				if (name.trim().length() <= 0)
					Toast.makeText(((AlertDialog)dialog).getContext(), 
							R.string.ask_to_valid_data, 
							Toast.LENGTH_LONG).show();
				else{
					
					if(editContact == null){
						Contact contact = new Contact();
						contact.name = name;
						contact.phone = phone;
						contacts.add(contact);
					}else{
						editContact.name = name;
						editContact.phone = phone;
					}
					contactAdapter.notifyDataSetChanged();
				}
				
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
	
	class ContactAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Contact contact = (Contact) getItem(arg0);
			View view = arg1;
			
			if(view == null)
				view = View.inflate(PotenzialOrg.this, R.layout.org_detail_info_row, null);
			
			view.setBackgroundResource(
					arg0 % 2 != 0 ? R.drawable.even_row_selector:  
									R.drawable.list_selector);
			
			view.setTag(contact);
			TextView tvFio = (TextView) view.findViewById(R.id.tvFio);
			String text = contact.name;
			if( contact.phone.length() > 0 )
				text += "\n" + contact.phone;
			tvFio.setText(text);
			
			return view;
		}
		
	}
	
	class MakePhoneCall implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
			Intent intent = new Intent(Intent.ACTION_CALL, 
					Uri.parse(String.format("tel: %s", 
							tvPhone.getText().toString())));
			startActivity(intent);
		}
		
	}
}
