package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgNotes;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgNotesImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.TaskImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.OrgInfoClickListener;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DialogOwner;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.RegDurationActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.PermissionChecker;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Helper который берет всю обработку Documents Activity на себ€ (все без ListView)
 * @author 1111
 *
 */
public class DocumentsBase extends RegDurationActivity implements Selector, DialogOwner, OrgInfoClickListener.ContactViewChanger {

	protected static final String ONLY_VISIT = "only_visit";
	
	private static final int WAIT_GPS_DLG_ID = 0;
	private static final int REMOVE_GPS_DLG_ID = 1;
	protected static final int NOTES_DLG_ID = 2;
	private static final int GPS_POS_RECIEVED = 3;
	private static final int GPS_VALID = 4;
	private static final int GPS_INVALID = 5;
	
	//public static final int ASK_FOR_OPEN_GPS = 6;
	public static final int DLG_WARNING_IF_ORG_IN_STOP_LIST = 10;

	//protected static final int ASK_FOR_PERMISSION = 7;  
	

	protected OrgImpl org;

	protected ImageButton btnNewDoc;
	protected ImageButton btnDocFilter;
	protected ImageButton btnGpsStatus;
	protected ImageButton taskBtn;

	private WaitGpsTimer waitGpsTimer;
	private Timer gpsObserver;		
	
	protected boolean allowCreateDocWhithoutGpsPos;

	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();

	protected boolean onlyVisit = false;
	private Dialog activeDialog;

	private DocType prevDocType = null;
	
	protected int getContentViewID() { return R.layout.documents; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getContentViewID());
		tvOrgInfo = (TextView) findViewById(R.id.tvOrgInfo);
		
		init((savedInstanceState != null) ? savedInstanceState : getIntent().getExtras());	

		if (getLastNonConfigurationInstance() != null){
			waitGpsTimer = (WaitGpsTimer)getLastNonConfigurationInstance();
			waitGpsTimer.setHandler(handler);
		}		
	
		allowCreateDocWhithoutGpsPos = Features.ALLOW_CREATE_DOC_WHITHOUT_GPS_POS || !NapoleonServiceW.isTracking();
		
		taskBtn = new ImageButton(this);
		taskBtn.setImageResource(R.drawable.task_doc_2);
		taskBtn.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { TaskImpl.editTask(org.getData().id, DocumentsBase.this); }
		});
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null)
			onlyVisit = bundle.getBoolean(ONLY_VISIT);
		
		if (onlyVisit){
			onlyVisitInit();
		}
	
		if( Features.QUESTION ) {
			View v = findViewById(R.id.btnSendDocList); 
			if( v != null ) {
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String where = "id='" + org.getData().id + "'";
						com.grsoft.napoleon.documents.DocList dl = new com.grsoft.napoleon.documents.DocList(AnswerImpl.class, where, "");
						
						if( dl.getCount() > 0 ) {
							DocSendListner docSend = new DocSendListner(QuestionDoc.instance().getObjectName(), dl);
							DocumentSender ds = new DocumentSender(DocumentsBase.this, findViewById(R.id.btnSendDocList), docSend, null);
							ds.execute((Void[])null);
						}
					}
				});
			}
		}
	}

	protected void onlyVisitInit() {
		if( DocType.getCurDoc() != VisitDoc.instance() )
			prevDocType  = (DocType) DocType.getCurDoc();
		
		DocType.setCurDoc(VisitDoc.instance());
		btnDocFilter.setOnClickListener(null);
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		Log.d(Consts.D_TAG,"onSaveInstanceState");
		outState.putString(ExtrasConst.ORG_ID_STR, org.getData().id);
	}
	
	@Override
	public void setActiveDialog(Dialog dlg) {
		activeDialog = dlg;
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if(gpsObserver != null){
			gpsObserver.cancel();
			gpsObserver = null;
		}		

		if (activeDialog != null){
			try{
				activeDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}			
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		   
		Log.d(Consts.D_TAG,"Documents.onResume");
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.llTaskBar);
		if( ll != null ) {
			if( TaskImpl.haveTask(org.getData().id) ) {
				int index = ll.indexOfChild(taskBtn);
				if( index < 0 ) {
					LayoutParams mp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
					View v = ll.findViewById(R.id.btnGpsStatus);
					if( v == null )
						v = ll.findViewById(R.id.btnNewDoc);
					ll.addView(taskBtn, ll.indexOfChild(v) + 1, mp);
				}
			} else {
				ll.removeView(taskBtn);
			}
		}

		DocType curDoc = (DocType) DocType.getCurDoc();
		if (curDoc != null)
			adjustViewForDocType(curDoc);
		
		if (NapoleonServiceW.isTracking()){
			gpsObserver = new Timer();
			gpsObserver.scheduleAtFixedRate(new TimerTask() {
			
				@Override
				public void run() {
					Log.d(Consts.D_TAG, "gpsGuard.scheduleAtFixedRate");
					
					if (waitGpsTimer != null)
						return;
					
					boolean gpsValid = isGpsPosValid();
					
					if (gpsValid)
						handler.sendEmptyMessage(GPS_VALID);
					else
						handler.sendEmptyMessage(GPS_INVALID);
					
				}
			}, Consts.ONE_SECOND, Consts.ONE_SECOND);
		} else
			btnGpsStatus.setVisibility(View.GONE);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (waitGpsTimer != null)
			return waitGpsTimer;
			
		return super.onRetainNonConfigurationInstance();
	}
	
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this);
	}
	
	protected boolean isOrgBlocked(Org o, DocType dt) {
		return (dt == SalesDoc.instance() || dt == OrderDoc.instance() || dt == ScriptDoc.instance()) && org.getData().isStopList();
	}
	
	protected void init(Bundle b) {
		String orgId = "";
		
		if(b != null){
			onlyVisit = b.getBoolean(ONLY_VISIT, false);
			orgId = b.getString(ExtrasConst.ORG_ID_STR);
		}
		
		org = new OrgImpl();
		org.getData().id = orgId;
		org.read();
		org.close();
		
		LinearLayout llHeader = (LinearLayout) findViewById(R.id.llHeader);
		
		if(llHeader != null)
			llHeader.setOnClickListener(createInfoClickListener());
		
		btnNewDoc = (ImageButton) findViewById(R.id.btnNewDoc);
		btnNewDoc.setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) { 
				super.onClick(v);
				
				DocType dt = (DocType) DocType.getCurDoc();
				if( isOrgBlocked(org.getData(), dt) )
					showDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
				else
					doCreate();
			}
		});
		
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData())));
		
		btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setOnClickListener(createDocFilter());

		initNotesDlg();

		btnGpsStatus = (ImageButton) findViewById(R.id.btnGpsStatus);
		btnGpsStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( isGPSTurnOn() == false ) 
					showDialog(R.id.ask_for_open_gps);
				else if (hasLocationPermission() == false)
					showDialog(R.id.ask_for_location_permission);
				else
					doGPSScan();
			}
		});
		
		onStatusChange(GPSUtilNew.isGpsAvailable());

	}

	protected boolean hasLocationPermission() {
		return PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
				PermissionChecker.PERMISSION_GRANTED;
	}

	protected void initNotesDlg() {
		String orgId = org.getData().id;
		OrgNotesImpl orgNotesImpl = new OrgNotesImpl();
		orgNotesImpl.getData().id = orgId;
		DbWriter.checkDBTable(DbObject.getDataType(orgNotesImpl.getData().getClass()));
		if (orgNotesImpl.read())
			showDialog(NOTES_DLG_ID);
		
		orgNotesImpl.close();
	}
	
	protected boolean isGPSTurnOn(){
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		return locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
	}
	
	protected void makeLocationAlert() {
		if ( hasLocationPermission() == false)
			showDialog(R.id.ask_for_location_permission);
		else if( isGPSTurnOn() == false ) 
			showDialog(R.id.ask_for_open_gps);
		else 
			Toast.makeText(DocumentsBase.this, R.string.gpscoord_is_old, Toast.LENGTH_LONG).show();
	}
	
	protected void doCreate() {
		docCreating();
	}

	protected void docCreating() {
		if (isGpsPosValid())
			createNewDoc();
		else
			makeLocationAlert();
	}
	
	protected boolean isGpsPosValid(){
		return allowCreateDocWhithoutGpsPos || GPSUtilNew.isGpsPosValid() || ConfigHelper.isValidOrgTime(this, org.getData().id);
	}
	
//	private static final String ID_ORG = "saved_id_org";
//	private static final String ORG_TIME = "ORG_TIME";
//
//	protected boolean isValidOrgTime(){
//		boolean result = false;
//		
//		SharedPreferences sp =  getPreferences(Context.MODE_PRIVATE);
//		String id = sp.getString(ID_ORG, "invalid_id_for_organization");
//		
//		if(id.equals(org.getData().id)){
//			long time = sp.getLong(ORG_TIME, Consts.INVALID_ID);
//			
//			if(time != Consts.INVALID_ID){
//				long now = new Date().getTime();
//				CfgNplW config = (CfgNplW) ConfigManager.getConfig();
//				
//				result =  (now - time) < config.gps_valid_in_org; 
//			}
//		}
//		
//		return result;
//	}
//
//	protected void saveOrgTime(){
//		Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
//		ed.putString(ID_ORG, org.getData().id);
//		ed.putLong(ORG_TIME, new Date().getTime());
//		ed.commit();
//	}
	
	
	@Override
	public void selectedType(DocType newDocType) {
		DocType curDoc = (DocType) DocType.getCurDoc();
		if( newDocType != null && (curDoc == null  || newDocType.equals(curDoc) == false) )
			adjustViewForDocType((DocType) newDocType);
	}
	
	protected boolean canCreateDoc(DocType docType) {
		if(docType == DebtDoc.instance() && DocType.getDocType(IncassDoc.instance().getObjectName()) != null)
			return true;
		return docType.isCreatable();
	}

	protected void adjustViewForDocType(DocType docType) {
		DocType prevDoc = (DocType) DocType.getCurDoc();
		if( prevDoc != null && prevDoc != docType )
			prevDoc.viewClosed(this);
		
		DocType.setCurDoc(docType);
		
		docType.viewOpened(this);
		
		boolean creatableDoc = canCreateDoc(docType);
		
		if(Features.BTN_NEW_DOC_INVISIBLE)
			btnNewDoc.setVisibility(creatableDoc ? View.VISIBLE : View.GONE);
		else
			btnNewDoc.setEnabled(creatableDoc);
		
		btnGpsStatus.setEnabled(true);
		
		ImageButton btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setImageResource(docType.getResurce2Id());

		refreshTotalSum();
	}
	
	protected void refreshTotalSum() {
		OrgSumImpl oi = new OrgSumImpl();
		OrgSum os = oi.getData();
		os.id = org.getData().id;
		os.type = DocType.getCurDoc().getName();
		oi.read();
		oi.close();
		updateTotalSum(os.sum, 0);
	}
	
	public void onStatusChange(boolean enable) {
//		if (enable)
//			btnGpsStatus.setVisibility(View.GONE);
//		else
//			btnGpsStatus.setVisibility(View.VISIBLE);
//		btnGpsStatus.setImageResource(enable ? R.drawable.gpsok: R.drawable.gpsno);
	}
	
	protected void createNewDoc() {
		ConfigHelper.saveValidOrgTime(this, org.getData().id);
		
		DocType dt = (DocType) DocType.getCurDoc();
		if( dt == DebtDoc.instance() ) {
			DocType id = IncassDoc.instance();
			if(DocType.getDocType(id.getObjectName()) != null)
				dt = id;
		}
		if( dt.isCreatable() ) {
			CreatableDocument<?> doc = (CreatableDocument<?>)dt.create();
			if( doc.init(this, org.getData().id, GPSUtilNew.getLastKnownLocation(this)))
				doc.open(this);
			doc.close();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(prevDocType != null)
			DocType.setCurDoc(prevDocType);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(Consts.D_TAG, "onCreateDialog " + Integer.toString(id));
		
		if(id == R.id.ask_for_open_gps)
			return CommonDialogs.createAskOpenGpsDialog(this);
			
		if(id == R.id.ask_for_location_permission)
			return CommonDialogs.createAskForPermissionDialog(this);

		
		Dialog result = null;
		switch(id){
		case WAIT_GPS_DLG_ID:
			result = ProgressDialog.show(this, "", getString(R.string.wait_connection_gps));
			result.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					Log.d(Consts.D_TAG, "ProgressDialog.onKey: keyCode=" + Integer.toString(keyCode));
					
					if (keyCode == KeyEvent.KEYCODE_BACK){
						if (waitGpsTimer != null)
							waitGpsTimer.cancel();
						
						removeDialog(WAIT_GPS_DLG_ID);
					}
					
					return true;
				}
			});
			break;
						
		case DLG_WARNING_IF_ORG_IN_STOP_LIST:
			return createWarningStopListDlg();

		case NOTES_DLG_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(View.inflate(this, R.layout.orgnotes, null));
			builder.setTitle(R.string.remark);
			
			builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText edNotes = (EditText)((AlertDialog) dialog).findViewById(R.id.edNotes);
					String text = edNotes.getText().toString();
					
					if (text.trim().length() == 0)
						return;
					
					OrgNotesImpl orgNotesImpl = new OrgNotesImpl();
					OrgNotes orgNotes = orgNotesImpl.getData();
					orgNotes.id = org.getData().id;
					orgNotes.text = text;
					orgNotes.date = Util.getDateTime();
					orgNotesImpl.write();
				}
			});
			
			builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					OrgNotesImpl orgNotesImpl = new OrgNotesImpl();
					orgNotesImpl.getData().id = org.getData().id;
					
					if (orgNotesImpl.read())
						orgNotesImpl.delete();
				}
			});
			result = builder.create();
		}
		
		return result;
	}
	
//	private Dialog createAskForPermissionDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(R.string.gps_permission_dissallow);
//		builder.setMessage(R.string.gps_permission_explain);
//		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent appSettingsIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
//		           Uri.parse("package:" + getPackageName()));
//				startActivity(appSettingsIntent);
//			}
//		});
//		
//		return builder.create();
//	}

//	private Dialog createAskOpenGpsDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(R.string.gpsOffTitle);
//		builder.setMessage(R.string.gpsOffMessage);
//		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
//			}
//		});
//		return builder.create();
//	}

	protected String getNonBlockingMessage(){
		return getString(R.string.client_in_stop_list) + " " +
				getString(R.string.order_cant_processing);
	}
	
	protected String getStopMessage() {
		return getString(R.string.client_in_stop_list);
	}
	
	protected Dialog createWarningStopListDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		if( isBlocked() ) {
			builder.setMessage(getStopMessage());
			builder.setPositiveButton(R.string.ok, null);			
		} else {
			builder.setMessage(getNonBlockingMessage());
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) { doCreate(); }
			});
			
			builder.setNegativeButton(R.string.cancel, null);
		}
		return builder.create();
	}

	protected boolean isBlocked() { return Features.BLOCK_IN_STOP_LIST || org.getData().isBlocked(); }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenuHelper.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Log.d(Consts.D_TAG, "onPrepareDialog " + Integer.toString(id) + " " + dialog.toString());
		switch (id)
		{
		case NOTES_DLG_ID:
			OrgNotesImpl orgNotesImpl = new OrgNotesImpl();
			orgNotesImpl.getData().id = org.getData().id;
			EditText edNotes = (EditText)dialog.findViewById(R.id.edNotes);
			edNotes.setText("");
			
			if (orgNotesImpl.read())
				edNotes.setText(orgNotesImpl.getData().text);
			
			break;
		}
	}
	
	/**
	 * «аголовок окна. ћожно использовать HTML теги
	 * @param o
	 * @return
	 */
	protected String orgInfo(Org o) {
		String ret = o.name;
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			ret += "<br/><i>" + o.address + "</i>";
		}
		if(o.info.length() > 0) {
			ret += "<br/>" + o.info;
		}
		return ret; 
	}
	
	protected OnClickListener createInfoClickListener(){
		return new OrgInfoClickListener(org.getData(), getContactViewid(), this); 
	}
	
	@Override
	public void setContactView(Contact contact, View view){}
	
	protected int getContactViewid(){
		return R.layout.org_detail_info_row;
	}
	
	public void doGPSScan() {
		GPSUtilNew.stop(this);
		GPSUtilNew.start(this);
		allowCreateDocWhithoutGpsPos = false;
		showDialog(WAIT_GPS_DLG_ID);
		waitGpsTimer = new WaitGpsTimer();
		waitGpsTimer.setHandler(handler);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.d(Consts.D_TAG, "handler.handleMessage: " + 
					Integer.toString(msg.what));
			switch(msg.what){
			case REMOVE_GPS_DLG_ID:
				waitGpsTimer = null;
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(DocumentsBase.this, R.string.allow_crete_doc_without_gps, Toast.LENGTH_LONG).show();
				allowCreateDocWhithoutGpsPos = true;
				onGPSFail();
				break;
			case GPS_POS_RECIEVED:
				waitGpsTimer = null;
				removeDialog(WAIT_GPS_DLG_ID);
				Toast.makeText(DocumentsBase.this, R.string.gps_received, Toast.LENGTH_LONG).show();
				onGPSReceived();
				break;
			case GPS_VALID:
				btnGpsStatus.setVisibility(View.GONE);
				break;
			case GPS_INVALID:
				btnGpsStatus.setVisibility(View.VISIBLE);
				break;
			}
		};
	};

	protected TextView tvOrgInfo;
	
	protected void onGPSReceived(){}
	protected void onGPSFail(){}
	
	class WaitGpsTimer extends Timer{
		private final int DELAY_TIME = 1000;
		private final int WAIT_TIME = ((CfgNplW)ConfigManager.getConfig())
				.waitGpsCoordOnRequest;
		private WGTimerTask task = new WGTimerTask();
		private Handler handler;
		private int couner; 
		
		public WaitGpsTimer(){
			scheduleAtFixedRate(task, DELAY_TIME, DELAY_TIME);
		}
		
		private void setHandler(Handler handler){
			this.handler = handler;
		}
		
		class WGTimerTask extends TimerTask{

			@Override
			public void run() {
				
				Log.d(Consts.D_TAG, "WGTimerTask.run: " + couner++);
				Log.d(Consts.D_TAG, "isGpsPosValid: " + GPSUtilNew.isGpsPosValid());
				
				if (GPSUtilNew.isGpsPosValid()){
					handler.sendEmptyMessage(GPS_POS_RECIEVED);
					cancel();
				} else if (couner >= WAIT_TIME){
					handler.sendEmptyMessage(REMOVE_GPS_DLG_ID);
					cancel();
				}
			}
		}
	}
	
	class OptionsMenuHelper {
		public static final int MNU_DOC_LIST_ID = 0;
		public static final int MNU_NEW_DOC_ID = 1;
		public static final int MNU_NOTES_ID = 2;
		
		public void onCreateOptionsMenu(Menu menu) {
			menu.add(Menu.NONE, MNU_DOC_LIST_ID, Menu.NONE, R.string.docs);
			
			if (DocType.getCurDoc().isCreatable())
				menu.add(Menu.NONE, MNU_NEW_DOC_ID, Menu.NONE, R.string.new_doc);
			
			menu.add(Menu.NONE, MNU_NOTES_ID, Menu.NONE, R.string.remark);
		}
		
		public void onOptionsItemSelect(MenuItem item) {
			switch(item.getItemId())
			{
				case MNU_DOC_LIST_ID:
					selectForNewDocType();
					break;
				case MNU_NEW_DOC_ID:
					createNewDoc();
					break;
				case MNU_NOTES_ID:
					showDialog(NOTES_DLG_ID);
					break;
			}
		}

		private void createNewDoc() {
			if (canCreateDoc(DocType.getCurDoc()) && btnNewDoc != null && btnNewDoc.isShown())
				btnNewDoc.performClick();
		}
		
		private void selectForNewDocType() {
			if (btnDocFilter != null)
				btnDocFilter.performClick();
		}
	}
}
