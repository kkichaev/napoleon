package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Forma;
import com.grsoft.dataobjects.OrgData;
import com.grsoft.dataobjects.PhotoItem;
import com.grsoft.dataobjects.impl.PicStoreImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class OrgEditActivity extends Activity implements OnClickListener, LocationListener  {
	public static final String DEL_PHOTO_ACTION = "com.grsoft.napoleon.OrgEditActivity.DEL_PHOTO_ACTION"; 
	private Button btnForma;
	private CheckBox cbCash;
	private EditText edNumber;
	private Button btnGPS;
	protected CreatableDocument<?> doc;
	private EditText edINN;
	private LinearLayout preview;
	private ImageButton btnPhoto;
	private static final String COUNTER = "counter_str";
	private String storePath = new String();
	protected static final int CAMERA_ACTIVITY = 1;
	private static final int INIT = 0;
	private static final int REMOVE_GPS_DLG_ID = 1;
	private static final int GPS_POS_RECIEVED = 2;
	private Location location;
	private SecondTimer secondTimer;
	private LocationManager locationManager;
	
	public interface OnFormaListener {
		void onSelect(Forma forma);
	};
	
	private Map<String, Forma> forms = new HashMap<String, Forma>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutId());
		
		btnForma = (Button) findViewById(R.id.btnForma);
		edINN = (EditText) findViewById(R.id.edINN);
		btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		preview = (LinearLayout) findViewById(R.id.preview);
		cbCash = (CheckBox) findViewById(R.id.cbCash);
		edNumber = (EditText) findViewById(R.id.edNumber);
		btnGPS = (Button) findViewById(R.id.btnGPS);
		
		this.doc = createDocument();
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgData data = (OrgData) doc.getData();
		locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		if (doc.isEditable()) {
			btnForma.setOnClickListener(this);
			btnPhoto.setOnClickListener(this);
			btnGPS.setOnClickListener(this);
		}
		
		DataTraveler.travel(Forma.class, new DataTraveler.Travel<Forma>(true) {

			@Override
			public boolean travel(DataTraveler<Forma> item) {
				forms.put(item.data.id, item.data);
				return true;
			}
		}, null);
		
		btnForma.setText(getFormName(data.forma));
		edINN.setText(data.inn);
		cbCash.setChecked(data.cash != 0);
		edNumber.setText(data.number);
		
		String g = getString(R.string.location_has_been_recieved);

		if (data.latitude == 0 && data.longitude == 0)
			g = getString(R.string.location_recieve);

		btnGPS.setText(g);
	}
	
	abstract CreatableDocument<?> createDocument();
	abstract int getLayoutId();

	public String getFormName(String id) {
		String res = id;
		
		if (forms.containsKey(id))
			res = forms.get(id).name;
		
		return res;
	}
	
	public int getINNLength(String id) {
		int res = 0;
		
		if (forms.containsKey(id))
			res = forms.get(id).innLength;
		
		return res;
	}
	
	public List<Forma> getForms(){
		return new ArrayList<Forma>(forms.values());
	}
	
	public Dialog createFormaDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final List<Forma> list = getForms();
		Collections.sort(list, new Comparator<Forma>() {

			@Override
			public int compare(Forma lhs, Forma rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});

		list.add(0, new Forma());

		ArrayAdapter<Forma> aa = new ArrayAdapter<Forma>(this, R.layout.simple_spinner_layout, list);
		builder.setAdapter(aa, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Forma f = list.get(which);
				formaListener.onSelect(f);
			}
		});
		
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.btnForma)
			showDialog(R.id.forma_dlg);
		else if (id == R.id.btnPhoto)
			doPhoto();
		else if (id == R.id.btnGPS)
			getLocation();
	}
	
	private void doPhoto() {
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File path = new File(Path.getDataDir());
				path.mkdir();
				SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
				int cnt = pref.getInt(COUNTER, 1);
				File file = new File(getExternalFilesDir(null), Integer.toString(cnt) + ".jpg");
				storePath = file.getAbsolutePath();
				Editor ed = pref.edit();
				ed.putInt(COUNTER, ++cnt);
				ed.commit();

				Uri uri = null;
				
				if (Build.VERSION.SDK_INT >= 24) {
					uri = FileProvider.getUriForFile(this,"com.grsoft.napoleon.fileprovider", file); 
				}else
					uri = Uri.fromFile(file);
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				startActivityForResult(intent, CAMERA_ACTIVITY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.forma_dlg)
			return createFormaDlg();
		else if (id == R.id.wait_dlg)
			return createWaitDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
	
	OnFormaListener formaListener = new OnFormaListener() {
		
		@Override
		public void onSelect(Forma forma) {
			OrgData data = (OrgData) doc.getData();
			data.forma = forma.id;
			doc.write();
			doc.close();
			
			btnForma.setText(forma.name);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		
		if (doc.isEditable()) {
			OrgData data = (OrgData) doc.getData();
			String inn = edINN.getText().toString().trim();
	
			if (getINNLength(data.forma) == inn.length())
				data.inn = inn;
			
			data.number = edNumber.getText().toString().trim();
			data.cash = cbCash.isChecked() ? 1 : 0;
			
			if (location != null){
				data.latitude = (int) (location.getLatitude() * Consts.GPS_SCALE);
				data.longitude = (int) (location.getLongitude() * Consts.GPS_SCALE);
			}
		}
	}
	
	protected void initPreview() {
		int w = (int) getResources().getDimension(R.dimen.previewPhotoWidth);
		int h = (int) getResources().getDimension(R.dimen.previewPhotoHight);
		
		int space = (int) getResources().getDimension(R.dimen.previewPhotoSpace);
		
		preview.removeAllViews();
		
		OrgData c = (OrgData) doc.getData();
		
		for(int i = 0; i < c.photos.size(); i++){
			String id  = c.photos.get(i).id;
			PicStoreImplEx picStore = new PicStoreImplEx();
			
			if (picStore.read("id", id)) {
				String p = new String(picStore.getData().picture);
				TextView t = new TextView(this);
				t.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.createBitmap(this, p, w, h), null, null);
				t.setOnLongClickListener(managePhoto);
				t.setTag(id);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 0, space, 0);
				lp.gravity = Gravity.CENTER_VERTICAL;
				t.setLayoutParams(lp);
				preview.addView(t);
			}
		}
	}

	OnLongClickListener managePhoto = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			ManagePhotoDlgEx dlg = new ManagePhotoDlgEx();
			Bundle args = new Bundle();
			args.putString(ManagePhotoDlgEx.PIC_ID, v.getTag().toString());
			dlg.setArguments(args);
			dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
			return true;
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK
				&& storePath.trim().length() > 0) {
			
			PicStoreImplEx picStore = new PicStoreImplEx();
			picStore.getData().id = UUID.randomUUID().toString().replace("-", "");
			picStore.getData().picture = storePath.getBytes();
			picStore.getData().date = doc.getData().created;
			picStore.write();
			picStore.close();
			
			PhotoItem item = new PhotoItem();
			item.id = picStore.getData().id;
			item.date = new Date();
			((OrgData)doc.getData()).photos.add(item);
			doc.write();
			doc.close();
			
			storePath = "";
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INIT:
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, OrgEditActivity.this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, OrgEditActivity.this);
				break;
			case REMOVE_GPS_DLG_ID:
				secondTimer = null;
				locationManager.removeUpdates(OrgEditActivity.this);
				dismissDialog(R.id.wait_dlg);
				Toast.makeText(OrgEditActivity.this, R.string.impossible_get_gps, Toast.LENGTH_LONG).show();
				break;

			case GPS_POS_RECIEVED:
				secondTimer = null;
				locationManager.removeUpdates(OrgEditActivity.this);
				dismissDialog(R.id.wait_dlg);
				Toast.makeText(OrgEditActivity.this, R.string.gps_recieved_succs, Toast.LENGTH_LONG).show();
				btnGPS.setText(getString(R.string.location_has_been_recieved));
				break;
			}
		};
	};

	private void getLocation() {
		showDialog(R.id.wait_dlg);
		location = null;
		secondTimer = new SecondTimer();
		secondTimer.setHandler(handler);
		handler.sendEmptyMessage(INIT);
	}
	
	public class SecondTimer extends Timer {
		private final int DELAY_TIME = 1000;// 1 sek;
		private final int WAIT_TIME = ((CfgNplW) ConfigManager.getConfig()).waitGpsCoordOnRequest;
		private WGSecondTask task = new WGSecondTask();
		private Handler handler;
		private int couner = 0;

		public SecondTimer() {
			scheduleAtFixedRate(task, DELAY_TIME, DELAY_TIME);
		}

		public void setHandler(Handler handler) {
			this.handler = handler;
		}

		class WGSecondTask extends TimerTask {

			@Override
			public void run() {
				Log.d(Consts.D_TAG, "WGTimerTask.run: " + couner++);

				if (location != null) {
					handler.sendEmptyMessage(GPS_POS_RECIEVED);
					cancel();
				} else if (couner >= WAIT_TIME) {
					handler.sendEmptyMessage(REMOVE_GPS_DLG_ID);
					cancel();
				}
			}
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initPreview();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(delphoto, new IntentFilter(DEL_PHOTO_ACTION));

	}
	
	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(delphoto);
	}
	
	BroadcastReceiver delphoto = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String p = intent.getStringExtra(ManagePhotoDlgEx.PIC_ID);
			
			OrgData v = (OrgData) doc.getData();
			for(PhotoItem i : v.photos){
				String s = new String(i.id);
				
				if(s.equals(p)){
					v.photos.remove(i);
					PicStoreImplEx picStore = new PicStoreImplEx();
					
					if (picStore.read("id",s)) {
						picStore.delete();
						picStore.close();
					}
					
					break;
				}
			}
			
			doc.write();
			doc.close();
			
			initPreview();
		}
	};

}
