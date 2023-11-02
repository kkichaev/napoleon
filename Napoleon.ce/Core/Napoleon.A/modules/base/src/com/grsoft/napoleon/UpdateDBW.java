/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма для обновления базы данных
 *
 * kki   07/10/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.grsoft.database.AnswerRestore;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DeliveryHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.OrgHitchingW;
import com.grsoft.database.PODelHitching;
import com.grsoft.database.PicStoreHitching;
import com.grsoft.database.PostUpdateDB;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.database.PotenzialOrgRcv;
import com.grsoft.database.PriceHitchingW;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportAnswerHitching;
import com.grsoft.database.RestoreDocProceeded;
import com.grsoft.database.TaskHitching;
import com.grsoft.database.TaskSendHitching;
import com.grsoft.database.VisitRestore;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FocusedGroup;
import com.grsoft.dataobjects.FocusedItems;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgStop;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.ReportList;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.dataobjects.impl.SyncInfoImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.BinaryFormat;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.NetworkBroadcasts;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.ProgressHelper.ButtonAction;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.VisitSendHelper;
import com.grsoft.network.WriteService;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.Util;
import com.grsoft.util.ViewInitializer;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.SimpleMessageBox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateDBW extends Activity implements ProgressHelper.ButtonAction{
	public static Class<? extends Activity> activity = UpdateDBW.class;
	private static final String TAG = "UpdateDB";
	protected UpdateProcess updateProcess;
	protected static Lock lock = new ReentrantLock();
	private static List<HitchingCtor> genDataHitchingCtors = new ArrayList<HitchingCtor>();
	private static List<HitchingCtor> restoreDataHitchingCtors = new ArrayList<HitchingCtor>();
	private static List<HitchingCtor> exportDataHitchingCtors = new ArrayList<HitchingCtor>();
	private static List<HitchingCtor> debetHitchingCtors = new ArrayList<HitchingCtor>();
	
	static public Class<? extends PriceHitchingW> priceHitchingClass = PriceHitchingW.class;
	/**
	 * Общие данные
	 */
	public static final int GEN_DATA_HITCHING = 1;
	/**
	 * Восстановить документы
	 */
	public static final int RESTORE_DATA_HITCHING = 2;
	/**
	 * Отправить документы
	 */
	public static final int EXPORT_DATA_HITCHING = 3;

	/**
	 * Прием долгов
	 */
	public static final int DEBET_DATA_HITCHING = 4;
	
	protected int traffic = 0;
	protected String errMessage = null;
	private static final String RUN_SYNC = "run_sync";  
	public static ViewInitializer initUI = new ViewInitializer();
	
	private boolean serviceBound = false;
	private NapoleonServiceW napoleonService;
	
	private boolean loadFullPrice = false;

	public static void addHitchingCtor(HitchingCtor ctor, int collection) {
		switch (collection) {
		case GEN_DATA_HITCHING:
			genDataHitchingCtors.add(ctor);
			break;
		case RESTORE_DATA_HITCHING:
			restoreDataHitchingCtors.add(ctor);
			break;
		case EXPORT_DATA_HITCHING:
			exportDataHitchingCtors.add(ctor);
			break;
		case DEBET_DATA_HITCHING:
			debetHitchingCtors.add(ctor);
		}
		
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override public void onServiceDisconnected(ComponentName name) { serviceBound = false; }

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			napoleonService = ((NapoleonServiceW.LocalBinder) service).getService();
			serviceBound = true;
		}
	};
	
	protected void onStart() {
		super.onStart();		

		Intent intent = new Intent(this, Napoleon.serviceType);
		boolean bindResult = getApplicationContext().
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.d(getClass().getCanonicalName(), 
				String.format("onStart: bindService %s", Boolean.toString(bindResult)));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	
		if (serviceBound) {
			getApplicationContext().unbindService(serviceConnection);
			serviceBound = false;
		}
	}

	static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);
	}

	static void openSync(Context context) {
		Intent i = new Intent(context, activity);
		i.putExtra(RUN_SYNC, true);
		context.startActivity(i);
	}
	
	protected int getContentView() {
		return R.layout.updatedb;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());
		initilizeUIComponent();

		// делаем так - мало ли что пошло не так и CostHitching не закрыл Reader
		BinaryFormat.BinaryReader = null;
		
		if(getIntent().getBooleanExtra(RUN_SYNC, false)){
			((CheckBox) findViewById(R.id.cbClearDB)).setChecked(true);
			((CheckBox) findViewById(R.id.cbDocs)).setChecked(false);
			((Button) findViewById(R.id.btnUpdate)).performClick();
		}
	}

	protected ScrollView scrollView;

	protected void initilizeUIComponent() {
		Button button = (Button) findViewById(R.id.btnUpdate);
		button.setOnClickListener(new StartUpdatesListener());

		Button btnSettings = (Button) findViewById(R.id.btnSettings);
		if (btnSettings != null)
			btnSettings.setOnClickListener(new SettingsListener());

		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(!Features.LOAD_FULL_PRICE);

		if (Features.COST_MANAGER == null)
			findViewById(R.id.cbCost).setVisibility(View.GONE);
		else {
			scrollView = (ScrollView) findViewById(R.id.svScroll);
			scrollView.post(new Runnable() {
				@Override
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		}
		
		((CheckBox) findViewById(R.id.cbVisit)).setChecked(Features.UPDATE_DB_CHECK_VISITS);

		if (Features.PRESENTATION_ON_SDCARD)
			findViewById(R.id.cbPresent).setVisibility(View.GONE);
		
		
		refreshSyncDates();
		
		initUI.init(this);
	}

	int usedFlags;
	private void refreshSyncDates() {
		if(Features.SYNC_INFO == false)
			return;
		
		long startDate = (new Date()).getTime() - 1000l * 32 * 3600 * 24;
		
		usedFlags = 0;
		DataTraveler.travel(SyncInfo.class, new DataTraveler.Travel<SyncInfo>() {

			@Override
			public boolean travel(DataTraveler<SyncInfo> item) {
				usedFlags = updateDate(item.data, usedFlags);
				return true;
			}
		}, "result = 1 and created >= " + Long.toString(startDate), "created desc");
	}
	
	boolean setSyncDate(SyncInfo data, int flag, int used, int id) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault());
		if( (data.syncparam & flag) != 0 && (used & flag) == 0 ) {
			TextView tv = (TextView)findViewById(id);
			if( tv != null ) {
				tv.setText(sdf.format(data.created));
				tv.setVisibility(View.VISIBLE);
			}
			return true;
		}
		
		return false;
	}

	protected int updateDate(SyncInfo data, int used) {
		if(setSyncDate(data, SyncInfo.CLEAR, used, R.id.tvSyncClearDB))
			used |= SyncInfo.CLEAR;

		if(setSyncDate(data, SyncInfo.GEN_DATA, used, R.id.tvSyncCommon))
			used |= SyncInfo.GEN_DATA;
		
		if(setSyncDate(data, SyncInfo.DOCS, used, R.id.tvSyncDocs))
			used |= SyncInfo.DOCS;

		if(setSyncDate(data, SyncInfo.VISIT, used, R.id.tvSyncVisits))
			used |= SyncInfo.VISIT;

		if(setSyncDate(data, SyncInfo.PRESENT, used, R.id.tvSyncPhoto))
			used |= SyncInfo.PRESENT;

		if(setSyncDate(data, SyncInfo.COST, used, R.id.tvSyncCost))
			used |= SyncInfo.COST;
		
		if(setSyncDate(data, SyncInfo.DEBT, used, R.id.tvSyncDebt))
			used |= SyncInfo.DEBT;
		
		return used;
	}

	@Override
	protected void onResume() {
		super.onResume();

		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			Adapter vda = sp.getAdapter();
			CfgNplW config = (CfgNplW) ConfigManager.getConfig();
			if (vda != null) {
				String checkStr = Integer.toString(config.monthsToRecreate);
				for (int i = 0; i < vda.getCount(); i++) {
					if (vda.getItem(i).toString().equals(checkStr)) {
						sp.setSelection(i, true);
						break;
					}
				}
			}
		}
	}

	protected void saveSettings() {
		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			CfgNplW c = (CfgNplW) ConfigManager.getConfig();
			c.monthsToRecreate = Integer
					.parseInt((String) sp.getSelectedItem());
			ConfigManager.save();
		}
	}

	/**
	 * 
	 * @param task
	 * @return true - показывать диалог с трафиком. false - не будет показывать
	 *         и выходить
	 */
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		return true;
	}

	class SettingsListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Setting.open(UpdateDBW.this);
		}
	}

	void addHitching(List<Hitching> list, HitchingCtor ctor) {
		List<Hitching> src = ctor.createList();
		if(src != null)
			list.addAll(src);
		
		Hitching h = ctor.create();
		if(h != null)
			list.add(h);
	}
	
	protected List<Hitching> getDebetHitching() {
		ArrayList<Hitching> debtHitchings = new ArrayList<Hitching>();
		DeliveryHitching dh = getDeliveryHitching(); 
		if(dh != null)
			debtHitchings.add(dh);
		debtHitchings.add(getPaymentHitching());
		
		for (HitchingCtor ctor : debetHitchingCtors)
			addHitching(debtHitchings, ctor);
		
		return debtHitchings;
	}

	protected DeliveryHitching getDeliveryHitching() {
		return new DeliveryHitching();
	}
	
	protected Hitching getPaymentHitching() {
		return new RcvNewHitching(DbObject.getDataType(Payment.class), "Payment");
	}

	protected Hitching getPriceHitching(boolean rcvRemains) {
		PriceHitchingW ph = null;
		try {
			ph = priceHitchingClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ph;
	}

	protected Hitching getOrgHitching() {
		return new OrgHitchingW();
	}
	
	public List<Hitching> getSyncData() {
		List<Hitching> result = new ArrayList<Hitching>();

		try {
			loadFullPrice = true;
			result = getGenDataHitchings();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		Log.d(TAG, "UpdateProcessEx.getHitchings");

		List<Hitching> result = new ArrayList<Hitching>();
		
		// конфиг должен прийти перед прайсом (это надо для приема прайса Сервико)
		result.add(new Hitching(com.grsoft.dataobjects.Config.class, "Config"));
		result.add(new Hitching(com.grsoft.dataobjects.Config.class, "ServerConfig"));

		for (HitchingCtor ctor : genDataHitchingCtors)
			addHitching(result, ctor);

		result.add(getOrgHitching());
		result.add(getPtncOrgHitching());

		result.add(new RcvNewHitching(DbObject.getDataType(Folder.class), "Folder"));

		Hitching ph = getPriceHitching(loadFullPrice);
		if( ph instanceof PriceHitchingW)
			((PriceHitchingW)ph).setPriceFilter(loadFullPrice);
		result.add( ph );
		
		Hitching orgFoldersHitching = getOrgFoldersHitching();
		if(orgFoldersHitching != null)
			result.add(orgFoldersHitching);
		
		result.add(new RcvNewHitching(Matrix.class, "Matrix"));
		result.add(new TaskHitching());

		// должно идти после PotenzialOrgRcv
		result.add(new PODelHitching());

		if (Features.FOCUSED_GROUP)
			result.add(new RcvNewHitching(DbObject.getDataType(FocusedGroup.class), FocusedGroupImpl.OBJECT_NAME));

		if (Features.FOCUSED_ITEMS)
			result.add(new RcvNewHitching(DbObject.getDataType(FocusedItems.class),FocusedItemsImpl.OBJECT_NAME));

		if (Features.SCRIPT_DOC)
			result.add(new RcvNewHitching(DbObject.getDataType(ScriptDef.class), ScriptDefImpl.OBJECT_NAME));

		if (Features.QUESTION)
			result.add(new RcvNewHitching(Question.class, "Question"));

		if (Features.ORG_STOP_TABLE) {
			result.add(new RcvNewHitching(OrgStop.class, "OrgStop"));
			Org.clearCache();
		}
		
		if(Features.REPORT_REQUEST) {
			result.add(new RcvNewHitching(ReportList.class));
			result.add(new ReportAnswerHitching());
		}
		
		if (Features.ORG_TASK)
			result.add(new Hitching(OrgTask.class, "OrgTask"));
		
		return result;
	}

	protected Hitching getOrgFoldersHitching() { 
		return new RcvNewHitching(OrgFolders.class, 
				Features.ROUTE_HISTORY ? "OrgCurrentRoute" : "OrgFolder"); 
	}

	protected PotenzialOrgRcv getPtncOrgHitching() {
		return new PotenzialOrgRcv();
	}

	boolean haveObject(List<Hitching> list, String objName) {
		for(Hitching h : list) {
			if(h.getObjectName().equals(objName))
				return true;
		}
		
		return false;
	}
	
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(createOrderRestore());
		result.add(new DocumentRestore(RemnantsDoc.instance()));
		result.add(new VisitRestore());
		
		if (Features.QUESTION)
			result.add(new AnswerRestore());

		if (Features.SCRIPT_DOC)
			result.add(new DocumentRestore(ScriptDoc.instance()));

		DocType incassDoc = IncassDoc.instance();
		if(DocTypeBase.getDocType(incassDoc.getObjectName()) != null && !haveObject(result,incassDoc.getObjectName()) )
			result.add(new DocumentRestore(incassDoc));
		
		for(HitchingCtor hc : restoreDataHitchingCtors)
			addHitching(result, hc);
		
		result.add(new RestoreDocProceeded());

		return result;
	}

	public DocumentRestore createOrderRestore() {
		return new DocumentRestore(OrderDoc.instance());
	}

	protected UserInfo getRcvUserInfo() {
		Config config = ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, config.impersonate, UpdateDBW.this);
	}

	protected UserInfo getSndUserInfo() {
		Config config = ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, config.impersonate, UpdateDBW.this);
	}

	protected UserInfo getGpsUserInfo() {
		Config config = ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, config.impersonate, UpdateDBW.this);
	}

	protected void postExported(boolean docExported) {
	}

	class UpdateProcess extends NetworkAsyncTask {
		private final static String TAG = "UpdateProcess";

		protected Activity activity;

		public UpdateProcess(Activity context) {
			super(new ProgressManager(context));
			((ProgressManager) this.progressHelper).setUpdateProcess(this);
			((ProgressManager) this.progressHelper).setButtonAction((ButtonAction) context);
			activity = context;
		}

		protected UpdateProcess(ProgressHelper progressHelper) {
			super(progressHelper);
		}

		/**
		 * Вызывается если нет ошибок в конце синхронизации для проведения
		 * дополнительного обмена
		 * @throws RuntimeException 
		 */
		protected void customSyncProcess() throws RuntimeException {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean res = false;

			SocketConnection activeCon = null;
			
			if (lock.tryLock()) {
				Log.d(TAG, "START Update");
				traffic = 0;
				errMessage = null;
				try {
					enableControlButton(false);
					onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

					CheckBox cbClearDB = (CheckBox) findViewById(R.id.cbClearDB);
					CheckBox cbDocs = (CheckBox) findViewById(R.id.cbDocs);
					CheckBox cbVisit = (CheckBox) findViewById(R.id.cbVisit);

					UserInfo sndUserInfo = getSndUserInfo();
					
					if (!isCancelled()
							&& (cbDocs.isChecked() || cbVisit.isChecked())
							&& sndUserInfo.isValid()) {

						boolean needSendPhotoDocs = false;
						List<CreateDocDataObject> photoDocs = new ArrayList<CreateDocDataObject>();
						List<DocExportListener> exportedDocs;
						exportedDocs = getExportedDocs(cbDocs.isChecked(), false);
						if (exportedDocs.size() > 0) {
							Log.d(TAG, "Docs are exporting");

							if(Features.UNLIMIT_VISIT_ITEMS) {
								List<DocExportListener> photas = new ArrayList<DocExportListener>();
								for(DocExportListener del : exportedDocs) {
									if(del.getObjectName().equals(VisitDoc.instance().getObjectName())) {
										photas.add(del);
										needSendPhotoDocs = true; // all new photas sends in visit brunch
//										DocList dl = del.getDocuments();
//										for(Document<?> d : dl) {
//											VisitImpl vi = (VisitImpl) VisitDoc.instance().create();
//											Visit v = vi.getData();
//											v.created = ((Visit)d.getData()).created;
//											vi.read();
//											vi.close();
//											if(vi.isExported())  
//												photoDocs.add(v);
//										}
//										dl.close();
									}
								}
								exportedDocs.removeAll(photas);
							}
							
							WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(exportedDocs);
							writeService.setUpdateProcessListenet(this);
							writeService.setActiveConnection(activeCon);

							if (!writeService.write(activity, sndUserInfo)) {
								errMessage = writeService.getMessage();
								Log.d(TAG, "Doc are exported: FAILURE");
							} else {
								activeCon = writeService.getActiveConnection();
								Log.d(TAG, "Doc are exported: SUCCESS");
								traffic += writeService.getSendedBytes();
							}
						}
						
						if(Features.UNLIMIT_VISIT_ITEMS) {
							if(errMessage == null && !isCancelled() && (needSendPhotoDocs || cbVisit.isChecked())) {
								for (DocTypeBase dt : DocTypeBase.docTypes) {
									List<CreateDocDataObject> pd = dt.getDirtyPhotos();
									if(pd != null && pd.size() > 0)
										photoDocs.addAll(pd);
								}
								if(photoDocs.size() > 0) {
									VisitSendHelper vsh = new VisitSendHelper();
									vsh.setActiveConnnection(activeCon);
									if( !vsh.send(UpdateDBW.this, sndUserInfo, photoDocs, this) ) {
										errMessage = vsh.getError();
										Log.d(TAG, "Visit are exported: FAILURE");
									} else {
										activeCon = vsh.getActiveConnect();
										Log.d(TAG, "Visit are exported: SUCCESS");
										traffic += vsh.getTraffic();
									}
										
								}
							}							
						} else {						
							while (true) {
								exportedDocs = getExportedDocs(false, cbVisit.isChecked());
	
								if (exportedDocs.size() > 0 && !isCancelled() && errMessage == null ) {
									Log.d(TAG, "Docs are exporting");
	
									WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(exportedDocs);
									writeService.setUpdateProcessListenet(this);
									writeService.setActiveConnection(activeCon);
	
									if (!writeService.write(activity, sndUserInfo)) {
										errMessage = writeService.getMessage();
										Log.d(TAG, "Doc are exported: FAILURE");
										break;
									} else {
										activeCon = writeService.getActiveConnection();
										Log.d(TAG, "Doc are exported: SUCCESS");
										traffic += writeService.getSendedBytes();
									}
								} 
								else
									break;
							}
						}
					}

					if (errMessage == null && !isCancelled()) {
						List<ObjectListener> docs = new ArrayList<ObjectListener>();
						docs.addAll(getExported());

						if(docs.size() > 0){
							WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs);
							writeService.setUpdateProcessListenet(this);
							writeService.setActiveConnection(activeCon);

							if (!writeService.write(activity, sndUserInfo)) {
								errMessage = writeService.getMessage();
								Log.d(TAG, "Doc are exported: FAILURE");
							} else {
								activeCon = writeService.getActiveConnection();
								Log.d(TAG, "Doc are exported: SUCCESS");
								traffic += writeService.getSendedBytes();
							}
						}
					}

					UserInfo gpsUserInfo = getGpsUserInfo();
					if (errMessage == null && !isCancelled() && sndUserInfo.isValid() && gpsUserInfo.impersonate.trim().length() == 0) {
						List<ObjectListener> docs = new ArrayList<ObjectListener>();

						GPSHitching gps = new GPSHitching();

						if (gps.size() > 0)
							docs.add(gps);

						LogHitching logHitching = new LogHitching();

						if (logHitching.needUpdate())
							docs.add(logHitching);

						if (docs.size() > 0) {
							Log.d(TAG, "GPSHitching are exporting");

							WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs);
							writeService.setUpdateProcessListenet(this);
							writeService.setActiveConnection(activeCon);

							if (!writeService.write(activity, gpsUserInfo)) {
								errMessage = writeService.getMessage();
								Log.d(TAG, "GPSHitching are exported: FAILURE");
							} else {
								activeCon = writeService.getActiveConnection();
								Log.d(TAG, "GPSHitching are exported: SUCCESS");
								traffic += writeService.getSendedBytes();
							}
						}
					}

					postExported(cbDocs.isChecked() && errMessage == null);

					if (errMessage == null && !isCancelled()
							&& cbClearDB.isChecked()) {

						Path.clearDataDir();
						DataBaseManager.clearBase();
						DbWriter.checkDBTable(OrgSum.class);
						LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "");
						Log.d(TAG, "Tables are cleared");
					}

					if (errMessage == null
							&& !isCancelled()
							&& ((CheckBox) findViewById(R.id.cbGenData))
									.isChecked()) {
						Log.d(TAG, "Gen data are importing");

						CheckBox cbRemains = null;
						try{
							cbRemains = (CheckBox) findViewById(R.id.cbRemains);
						}catch(Exception e){}
						
						loadFullPrice = cbRemains != null && cbRemains.isChecked();  
						List<Hitching> rcvHitch = getGenDataHitchings();

						if (rcvHitch.size() > 0) {
							UserInfo rcvUserInfo = getRcvUserInfo();
							if(rcvUserInfo.impersonate.trim().length() > 0)
								for(Hitching hitch : rcvHitch)
									hitch.impersonate(rcvUserInfo.impersonate);

							ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
									.createReadService(rcvHitch);
							dataBaseUpdater.setUpdateProcessListenet(this);
							dataBaseUpdater.setActiveConnection(activeCon);

							FoldersAdapter.resetCache();

							if (!dataBaseUpdater.update(activity,
									rcvUserInfo, false)) {
								errMessage = dataBaseUpdater.getMessage();
								Log.d(TAG, "Gen data are imported: FAILURE");
							} else {
								activeCon = dataBaseUpdater.getActiveConnection();
								Log.d(TAG, "Gen data are imported: SUCCESS");
								traffic += dataBaseUpdater.getReceivedBytes();
							}
						}
					}

					if (errMessage == null && !isCancelled()) {
						if (((CheckBox) findViewById(R.id.cbPresent))
								.isChecked()) {
							List<Hitching> result = getPrezentHitching();

							ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
									.createReadService(result);
							dataBaseUpdater.setUpdateProcessListenet(this);
							dataBaseUpdater.setActiveConnection(activeCon);

							if (!dataBaseUpdater.update(activity,
									getRcvUserInfo(), false)) {
								errMessage = dataBaseUpdater.getMessage();
								Log.d(TAG, "Gen data are imported: FAILURE");
							} else {
								dataBaseUpdater.setActiveConnection(activeCon);
								Log.d(TAG, "Gen data are imported: SUCCESS");
								traffic += dataBaseUpdater.getReceivedBytes();
							}
						}
					}

					if (errMessage == null && !isCancelled()) {
						CheckBox rcvCost = (CheckBox) findViewById(R.id.cbCost);
						if (rcvCost != null && rcvCost.isChecked()) {

							List<Hitching> rcvHitch = getCostHitching();
							ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
									.createReadService(rcvHitch);
							dataBaseUpdater.setUpdateProcessListenet(this);
							dataBaseUpdater.setActiveConnection(activeCon);

							if (!dataBaseUpdater.update(activity,
									getRcvUserInfo(), false)) {
								errMessage = dataBaseUpdater.getMessage();
								Log.d(TAG, "Gen data are imported: FAILURE");
							} else {
								dataBaseUpdater.setActiveConnection(activeCon);
								Log.d(TAG, "Gen data are imported: SUCCESS");
								traffic += dataBaseUpdater.getReceivedBytes();
							}
						}
					}

					CheckBox cbRecreateStory = (CheckBox) findViewById(R.id.cbRecreateStory);

					if (errMessage == null && !isCancelled()
							&& cbRecreateStory.isChecked()) {
						Log.d(TAG, "Order story is recreating");

						List<Hitching> recreateHitchings = getRestoreHitching();

						ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
								.createReadService(recreateHitchings);
						dataBaseUpdater.setUpdateProcessListenet(this);
						dataBaseUpdater.setActiveConnection(activeCon);

						if (!dataBaseUpdater.update(activity, getRcvUserInfo(),
								false)) {
							Log.d(TAG, "Order story is recreated: FAULURE");
							errMessage = dataBaseUpdater.getMessage();
						} else {
							dataBaseUpdater.setActiveConnection(activeCon);
							Log.d(TAG, "Order story is recreated: SUCCESS");
							traffic += dataBaseUpdater.getReceivedBytes();
						}
					}

					CheckBox cbDebt = (CheckBox) findViewById(R.id.cbDebt);

					if (errMessage == null && !isCancelled()
							&& cbDebt.isChecked()) {
						Log.d(TAG, "Debts are importing");

						List<Hitching> debtHitchings = getDebetHitching();

						ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
								.createReadService(debtHitchings);
						dataBaseUpdater.setUpdateProcessListenet(this);
						dataBaseUpdater.setActiveConnection(activeCon);
						//dataBaseUpdater.setPostUpdateWork(new PostUpdateDB());

						if (!dataBaseUpdater.update(activity, getRcvUserInfo(),
								false)) {
							errMessage = dataBaseUpdater.getMessage();
							Log.d(TAG, "Debts are imported: FAILURE");
						} else {
							activeCon = dataBaseUpdater.getActiveConnection();
							Log.d(TAG, "Debts are imported: SUCCESS");
							traffic += dataBaseUpdater.getReceivedBytes();
						}
					}

					if (errMessage == null && !isCancelled()) {
						PostUpdateDB pdb = new PostUpdateDB();
						pdb.run();
						customSyncProcess();
					}

					if (!isCancelled())
						onUpdate(UpdateStatus.END_OF_PROCESS, 0);

					if (!isCancelled()) {
						if (errMessage != null) {
							showErrorMsg(errMessage, activity);
							return res;
						} else {
							if (onFinishUpdate(this)) {
								res = true;

								SimpleMessageBox smb = new SimpleMessageBox(
										getString(R.string.inform),
										getString(R.string.sync_end_traffic)
												+ Integer
														.toString((traffic + 512) / 1024)
												+ " " + getString(R.string.kB),
										activity);
								onUpdateMessage(smb);
								Thread.sleep(3000);
								smb.hide();
							}
						}
					}

					CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
					
					if (cfg.day_to_del_visit > 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.HOUR, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						calendar.add(Calendar.DAY_OF_MONTH,
								-cfg.day_to_del_visit);

						String where = String.format(Locale.US, "created < %d",
								calendar.getTime().getTime());
						
						deleteVisits(where);
						deletePics(where);
					}

					if (Features.PRESENTATION_ON_SDCARD && cbClearDB.isChecked()) {
						SharedPreferences pref = getApplication()
								.getSharedPreferences(
										PresentSdcard.PREF_NAME,
										Context.MODE_PRIVATE);

						Editor ed = pref.edit();
						ed.putLong(PresentSdcard.UPDTATE_PRESENT_TIME, -1);
						ed.commit();
					}
					
					Log.d(TAG, "END UPDATE");
					
					DataBaseManager.closeAndInit();
					
					return res;
				} catch (Exception exception) {
					SQLiteDatabase dataBase = DataBaseManager.getDataBase();
					
					if (dataBase.isDbLockedByCurrentThread()
							|| dataBase.isDbLockedByOtherThreads()) {
						try {
							dataBase.endTransaction();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					errMessage = exception.getMessage();
					if (errMessage == null)
						errMessage = activity.getString(R.string.recieved_error);
					if (!isCancelled())
						showErrorMsg(errMessage, activity);

					exception.printStackTrace();

					return false;
				} finally {
					enableControlButton(true);
					Log.d(TAG, "finally END finally");
					
					try{
						lock.unlock();
					}catch(Exception e){}
				}
			} else {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								activity,
								Html.fromHtml(activity
										.getString(R.string.use_sync_later)),
								Toast.LENGTH_LONG).show();

					}
				});

				return false;
			}

		}

		protected void deletePics(String where) {
			com.grsoft.napoleon.documents.DocList dl = new DocList(PicStoreImpl.class, where, null);
			
			int count_to_del = dl.getCount();

			if (count_to_del > 0) {
				for (int i = 0; i < count_to_del; i++)
					((PicStoreImpl) dl.get(i)).delete();

				Log.d(TAG, "PicStore " + count_to_del
						+ " has been deleted");
			}
			
			dl.close();
		}

		protected void deleteVisits(String where) {
			VisitDoc vd = (VisitDoc) VisitDoc.instance();
			com.grsoft.napoleon.documents.DocList dl = vd.docList(
					null, null, where);

			int count_to_del = dl.getCount();

			if (count_to_del > 0) {
				for (int i = 0; i < count_to_del; i++)
					((VisitImpl) dl.get(i)).delete();

				try {
					vd.refreshDocSum();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.d(TAG, "Visits " + count_to_del
						+ " has been deleted");
			}

			dl.close();
		}

		class EnableButton implements Runnable {

			Button button;
			boolean enabled;

			public EnableButton(Button b, boolean e) {
				button = b;
				enabled = e;
			}

			@Override
			public void run() {
				button.setEnabled(enabled);
			}

		}

		protected void enableControlButton(boolean enabled) {
			Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
			btnUpdate.post(new EnableButton(btnUpdate, enabled));
		}

		private int getFVal(int id, int flag){
			int result = 0;
			CheckBox cb = (CheckBox) findViewById(id);
			
			if(cb != null && cb.isChecked())
				result = flag;
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			runOnUiThread(new Runnable() {
				@Override public void run() { getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); }
			});
			
			enableControlButton(true);
			
			if(Features.SYNC_INFO){
				SyncInfoImpl syncInfoImpl = new SyncInfoImpl();
				SyncInfo syncInfo = syncInfoImpl.getData();
				syncInfo.created = Util.getDateTime();
				syncInfo.syncparam = getFVal(R.id.cbClearDB, SyncInfo.CLEAR) +
						getFVal(R.id.cbGenData, SyncInfo.GEN_DATA) +
						getFVal(R.id.cbDocs, SyncInfo.DOCS) + 
						getFVal(R.id.cbVisit,SyncInfo.VISIT) +
						getFVal(R.id.cbPresent, SyncInfo.PRESENT) + 
						getFVal(R.id.cbCost, SyncInfo.COST) + 
						getFVal(R.id.cbDebt, SyncInfo.DEBT) +
						getFVal(R.id.cbRecreateStory, SyncInfo.RESTORE);
				syncInfo.params = 0;
				syncInfo.result = result ? 1 : 0;
				syncInfo.errmsg = errMessage == null ? "" : errMessage;
				
				Config cfg = ConfigManager.getConfig();
				syncInfo.login = cfg.login;
				syncInfo.password = cfg.passw;
				syncInfo.ip1 = cfg.address;
				syncInfo.ip2 = cfg.address2;
				syncInfo.port1 = cfg.port;
				syncInfo.port2 = cfg.port2;
				syncInfo.deviceID = ServerCommand.DeviceID;
				
				Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
				if (sp != null) {
					try{
						syncInfo.restore = Integer
								.parseInt((String) sp.getSelectedItem());
					}catch(Exception e){
						e.printStackTrace();
						syncInfo.restore = 0;
					}
				}
				
				syncInfoImpl.write();
				syncInfoImpl.close();
			}
			
			if (NapoleonServiceW.isTracking()){
				GPSUtilNew.stop(activity);
				GPSUtilNew.start(activity);
			}else
				GPSUtilNew.stop(activity);

			if (!showRecievedMessage(result ? new Runnable() {
				@Override
				public void run() {
					closeActivity();
				}
			} : null) && result == true)
				closeActivity();

			NapoleonServiceW.setUpdatePocessActive(false);

			if (serviceBound)
				napoleonService.update();
			
			PresentationFolderW.items.fill(false);
			NetworkBroadcasts.sendSyncResult(UpdateDBW.this, result);
			postSync(result);
		}

		@Override
		protected void onPreExecute() {
			NapoleonServiceW.setUpdatePocessActive(true);
			
			runOnUiThread(new Runnable() {
				@Override public void run() { getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); }
			});
		}
	}

	class StartUpdatesListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			saveSettings();
			UpdateProcess upd = getUpdateProcess();
			if( upd != null )
				upd.execute((Void[]) null);
		}
	}

	protected UpdateProcess getUpdateProcess() {
		Log.d(TAG, "UpdateDB.getUpdateProcess");
		updateProcess = new UpdateProcess(this);
		return updateProcess;
	}

	protected void postSync(Boolean result) {
	}

	protected List<DocExportListener> getExportedDocs(boolean docs, boolean visit) {
		return DocType.getDocuments(docs, visit);
	}
	
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = new ArrayList<ObjectListener>();

		for(HitchingCtor ctor: exportDataHitchingCtors){
			Hitching h = ctor.create();			
			addExportHitching(result, h);
			
			List<Hitching> src = ctor.createList();
			if( src != null)
				for(Hitching hs : src)
					addExportHitching(result, hs);
		}
			
		ObjectExportListener ol = new PotenzialOrgHitching();
		if (ol.size() > 0)
			result.add(ol);

		ol = new TaskSendHitching();
		if (ol.size() > 0)
			result.add(ol);
		
		ol = new PicStoreHitching();
		if (ol.size() > 0)
			result.add(ol);

		return result;
	}

	private void addExportHitching(List<ObjectListener> result, Hitching h) {
		if(h instanceof ObjectExportListener){
			ObjectExportListener oel = (ObjectExportListener)h;				
			if(oel.size() > 0)
				result.add(oel);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");

		if (updateProcess != null){
			updateProcess.cancel(false);
			
			try{
				lock.unlock();
			}catch(Exception e){}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected List<Hitching> getPrezentHitching() {
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(new PricePhotoHitching());
		return result;
	}

	protected List<Hitching> getCostHitching() {
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(Features.COST_MANAGER.getReceiveHitching(this));
		return result;
	}

	protected void closeActivity() {
		if(!Features.KEEP_DIALOG_AFTER_SYNC)
			finish();
	}

	@Override
	public void progressClosed() {
		finish();
	}
}
