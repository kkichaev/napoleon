package com.grsoft.napoleon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.PriceHitching;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgNotes;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ByteStream;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.NapoleonService;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;


public class NapoleonAppBase extends Application {
	private IShared shared;
	private static final String LOGIN_PREF = "LoginData";
	private static final String DURATION_KEY = "Duration";
	
	boolean inited = false;
	
	@Override
	public void onCreate() {
		ConfigManager.tryInitConfig(createConfig());
		
		super.onCreate();
		
		FirstRunInit.init(this);
		initDeviceID();
		Napoleon.serviceType = getServiceType();
		bindExchangeService();
		
		if (!Features.IS_MARKET_VERSION)
			Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
		
		UpdateDB.priceHitchingClass = PriceHitching.class;
		
		try {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			int maxMem = am.getMemoryClass();
			
			ByteStream.MAX_BUF_LENGTH = (maxMem * 3) / 4 * 1024 * 1024;			
			Log.d("onCreate", "memoryClass:" + Integer.toString(maxMem) + " max buffer:" + Integer.toString(ByteStream.MAX_BUF_LENGTH));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		defineNewType();
		initDocTypes();
				
		if(Features.KEEP_NOTES_ON_CLEAR_DB)
			DataBaseManager.dontDropTableNames.add(new OrgNotes().getTableName());
	}
	
	protected CfgNpl createConfig() { return new CfgNpl(); }

	protected void defineNewType() {}

	protected void initFeatures() {
		initResourceFeatures();
		
		Features.SYNC_INFO = true;
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
		Features.SHOW_DAILY_SALES_IN_WAREHOUSE = true;
		Features.SHOW_PRESENT_IMG = true;
		Features.SCRIPT_DOC = true;
		
		if (Features._362) {
			Features.HAVE_PRICE_MOVER = true;
			Features.DDLV = true;
			Features.DOC_STATUS_IN_DOC_LIST = true;
			Features.INCASS_DEBET_DISTRIB = true;
			Features.MAX_FOTO_HEIGHT = 4000;
			Features.MAX_FOTO_WIDTH = 4000;
			Features.PACK_INPUT = true;
			Features.RECEIVE_REMNANTS_WHEN_SENDING = true;
			Features.SHOW_NUMBER_IN_ORDER = true;
			Features.DEL_VISIT_WITHOUT_PHOTO = true;
			Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
			Features.CAN_SEND_EMPTY_DOCS = true;
			Features.REQUSET_FOCUS_IN_SEARCH = true;
			Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
			Features.COUNT_DOCS_IN_DOCSLIST = true;
			Features.UPDATE_PRICE_BACKGROUND = true;
			Features.CHECK_UNCOMPLETE_SCRIPTS = true;
			Features.SEND_IN_BACKGROUND = true;
			Features.REPORT_REQUEST = true;
			Features.KEEP_NOTES_ON_CLEAR_DB = true;
			Features.UNLIMIT_VISIT_ITEMS = true;
		}
		
		initChildFeature();
	}

	private void initResourceFeatures() {
		String pn = getPackageName();
		
		try {
			for (Field f :Features.class.getFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					int rid = getResources().getIdentifier(f.getName(), "string", pn);
					
					if (rid != 0) {
						if (f.getType() == boolean.class) {
							f.setBoolean(null, Boolean.parseBoolean(getString(rid)));
						}else if (f.getType() == int.class){
							f.setInt(null, Integer.parseInt(getString(rid)));
						}
					}
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void initChildFeature() {}

	protected void initAcivity() {
		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;

		if(!UpdateDB.class.isAssignableFrom(UpdateDBW.activity))
			UpdateDBW.activity = UpdateDB.class;
		
		Setting.WarehouseSettingActivity = WarehouseSetting.class;
		Setting.BehaviorSettingActivity = BehaviorSetting.class;
		PriceCountW.activity = PriceCount.class;
		QuestionWebView.activity = QuestEdit.class;

		if(!Documents.class.isAssignableFrom(DocumentsW.activity))
			DocumentsW.activity = Documents.class;
		
		initChildActivity();
	}
	
	protected void initChildActivity() {}

	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImpl.class; }

	protected void initDocTypes(){
		if(inited)
			throw new RuntimeException("Already inited");
		
		inited = true;
		// сложная инкассация включается фичей, фичи надо инициализировать перед документами
		initFeatures();

		DocType.addType(OrderDoc.instance(orderImplType()));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(remantsImplType()));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(ScriptDoc.instance(scriptImplType()));
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		
		if (Features._362) {
			DocType.addType(ReturnDoc.instance());
		}
		
		initChildDocTypes();
	
		setDefDocType();

		initAcivity();
	}
	
	
	protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImpl.class;	}

	protected Class<? extends RemnantsImpl> remantsImplType() { return RemnantsImpl.class; }

	protected void initChildDocTypes() {}

	private void bindExchangeService() {
		if (ConfigManager.getConfig().impersonate.trim().length() > 0) {
			Intent shared = new Intent("com.grsoft.napoleon.ExchangeService");
			shared.setPackage(this.getPackageName());
			bindService(shared, conn, Context.BIND_AUTO_CREATE);
		}
		
	}

	public void startMainService() {
		Intent intent = new Intent(this, getServiceType());
		startService(intent);
	}

	protected void initDeviceID() {
		try {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if( tm != null )
				ServerCommand.DeviceID = tm.getDeviceId();
			if(ServerCommand.DeviceID == null || ServerCommand.DeviceID.length() == 0) {
				ServerCommand.DeviceID = "000000000000000";
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	
	protected Class<? extends Service> getServiceType(){ return NapoleonService.class; }

	public void exit() {
		if (!((CfgNplW)ConfigManager.getConfig()).isAutostart) {
			Intent intent = new Intent(this, getServiceType());
			stopService(intent);
		}
	}
	
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			shared = IShared.Stub.asInterface(service);

			try {
				SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor e = prf.edit();
				int duration = shared.getDuration();
				e.putInt(DURATION_KEY, duration);
				e.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	public void setDefDocType() {
		DocType.setCurDoc(OrderDoc.instance());		
	}
}
