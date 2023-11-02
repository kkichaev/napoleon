package com.grsoft.ads;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.grsoft.ads.database.ClientHitching;
import com.grsoft.ads.database.FoldersHitching;
import com.grsoft.ads.database.OrderDelHitching;
import com.grsoft.ads.database.OrderRcvHitching;
import com.grsoft.ads.database.OrderRestore;
import com.grsoft.ads.database.PriceHitching;
import com.grsoft.ads.database.ServerConfigHitching;
import com.grsoft.ads.database.WorkDayHitching;
import com.grsoft.ads.dataobjects.AgentPrefix;
import com.grsoft.ads.dataobjects.impl.WorkDayImpl;
import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.ads.utils.LockOwner;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ProgressValue;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.network.UserInfo;
import com.grsoft.view.SimpleMessageBox;

class UpdateProcess extends NetworkAsyncTask
{
	protected UserInfo userInfo;
	private final static String TAG = "UpdateProcess";
	protected int traffic = 0;
	protected Context context;
	private Runnable postWorker;
	private LockOwner lockOwner;
	private boolean recreateOrder = false;
	public static Class<? extends UpdateProcess> processType = UpdateProcess.class;
	
	public static UpdateProcess createProcess(Context context, LockOwner lockOwner){
		try{
			Constructor<? extends UpdateProcess> cns 
				= processType.getConstructor(Context.class, LockOwner.class);
			return cns.newInstance(context, lockOwner);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public UpdateProcess(Context context, LockOwner lockOwner){
		super(context instanceof Activity ? new ProgressManagerAds(context) : null);
		
		if (context instanceof Activity)
			((ProgressManager)this.progressHelper).setUpdateProcess(this);
			
		this.context = context;
		this.lockOwner = lockOwner;
	}
	
	public void setRecreateOrder(boolean value){
		recreateOrder = value;
	}
	
	public void setPostWorker(Runnable run){
		postWorker = run;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		if(lockOwner.getLock().tryLock()){
			Log.d(TAG, "START Update");
			
			try
			{
				enableControlButton(false);
				onUpdate(UpdateStatus.BEGIN_UPDATE, 0);
				
				WorkDayImpl.closePrevDay();
				
				ConfigReader config = (ConfigReader) ConfigManager.getConfig(); 
				userInfo = new LoginData(config.getLogin(), config.getPassword(), context);
				
				String errMessage = null;
				
				if (!isCancelled()){
					List<ObjectListener> docs = createExportData();
					
					if(docs != null && docs.size() > 0){
						WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(docs);
						writeService.addRecieveHitch(new MessageHitching());
						writeService.setUpdateProcessListenet(this);
						
						if (!writeService.write(context, userInfo)){
							errMessage = writeService.getMessage();
							Log.d(TAG, "Doc are exported: FAILURE");
						}else{
							Log.d(TAG, "Doc are exported: SUCCESS");
							traffic += writeService.getSendedBytes();
						}
					}
				}

				if (errMessage == null && !isCancelled()) {
					Log.d(TAG, "Gen data are importing");
					
					List<Hitching> rcvHitch = createImportData();
					
					ReadServiceBase dataBaseUpdater =  RWServiceFactory.instance.createReadService(rcvHitch);
					dataBaseUpdater.setUpdateProcessListenet(this);
					
					if( !dataBaseUpdater.update(context, userInfo, false) ){
						errMessage = dataBaseUpdater.getMessage();
						Log.d(TAG, "Gen data are imported: FAILURE");
					}else{
						Log.d(TAG, "Gen data are imported: SUCCESS");
						traffic += dataBaseUpdater.getReceivedBytes();
					}
				}
				
				if (!isCancelled())
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
				if (!isCancelled() && context != null && context instanceof Activity){
					if( errMessage != null ) {
						showErrorMsg(errMessage, context);
						return false;
					} else {
						onFinishUpdate();
						
						SimpleMessageBox smb = new SimpleMessageBox("Информация", 
								"Синхронизация завершена\nТрафик: " + 
								Integer.toString((traffic + 512) / 1024) + " кБ", context); 
						onUpdateMessage(smb);
						Thread.sleep(3000);
						smb.hide();
					}
				}
				
				Log.d(TAG, "END UPDATE");
				return true;
			}
			catch(Exception exception)
			{
				SQLiteDatabase dataBase = DataBaseManager.getDataBase();
				
				if (dataBase.isDbLockedByCurrentThread()
						|| dataBase.isDbLockedByOtherThreads())
					dataBase.endTransaction();
				
				if (context != null && context instanceof Activity){
					String message = exception.getMessage();
					
					if( message == null )
						message = "Ошибка при приеме";
					
					if (!isCancelled())
						showErrorMsg(message, context);
				}
				exception.printStackTrace();
				
				return false;
			}
			finally
			{
				enableControlButton(true);
				Log.d(TAG, "finally END finally");
				lockOwner.getLock().unlock();
			}
		}else{
			if (context != null && context instanceof Activity)
				((Activity)context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(
								context, Html.fromHtml("Невозможно выполинть синхронизацию. <br>" +
										"Пожалуйста, повторите операцию через 2 минуты...."), Toast.LENGTH_LONG)
								.show();
						
					}
				});
			
			return false;
		}
	}

	protected List<Hitching> createImportData() {
		List<Hitching> rcvHitch = new ArrayList<Hitching>();
		
		rcvHitch.add(new OrderRcvHitching(context));
		rcvHitch.add(new ClientHitching());
		rcvHitch.add(new OrderDelHitching());
		rcvHitch.add(new FoldersHitching());
		rcvHitch.add(new PriceHitching());
		rcvHitch.add(new ServerConfigHitching());
		rcvHitch.add(new RcvNewHitching(com.grsoft.dataobjects.Config.class, "Config"));
		rcvHitch.add(new RcvNewHitching(AgentPrefix.class, "Agents"));
		rcvHitch.add(new MessageHitching());
		
		if (recreateOrder)
			rcvHitch.add(new OrderRestore());
		
		return rcvHitch;
	}

	protected List<ObjectListener> createExportData() {
		List<ObjectListener> docs = new ArrayList<ObjectListener>();
		
		docs.addAll(DocType.getDocuments(true, false));
		GPSHitching gps = new GPSHitching();
		
		if( gps.size() > 0 )
			docs.add(gps);
		
		LogHitching logHitching = new LogHitching();
		
		if (logHitching.needUpdate())
			docs.add(logHitching);
		
		WorkDayHitching wdh = new WorkDayHitching();
		if (wdh.size() > 0)
			docs.add(wdh);
		return docs;
	}

	private void onFinishUpdate() {
		
	}

	private void enableControlButton(boolean b) {
	}
	
	@Override
	protected void onPreExecute() {
		//((AdsService)GlobalServiceContext.service).gpsStop();
	}
	
	protected void onPostExecuteWork(Boolean result){
//		ConfigImpl configImpl = new ConfigImpl();
//		configImpl.getData().key = GpsTrackingManager.KEY_VAL;
//		configImpl.getData().value = GpsTrackingManager.GPS_ROUTE_ID;
//		configImpl.write();
//		configImpl.close();
		
		//((AdsService)GlobalServiceContext.service).gpsInit();
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		onPostExecuteWork(result);
		if (postWorker != null && result)
			postWorker.run();
	}
	
	@Override
	protected boolean showRecievedMessage(Runnable doAfterDialog) {
		boolean result = super.showRecievedMessage(doAfterDialog);
		
		if(result){
			Notification notify = new Notification();
			SharedPreferences pref = context.getSharedPreferences(
					Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			
			String message_snd = pref.getString(Setting.MESSAGE_SND, "");
			
	        Log.d(TAG, message_snd);
			notify.sound = Uri.parse(message_snd);
			
			if (pref.getBoolean(Setting.VIBRATE, false))
				notify.defaults |= Notification.DEFAULT_VIBRATE;
				
			((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notify);
		}
		
		return result;
	}
}

class ProgressManagerAds extends ProgressManager
	implements OnCancelListener
{
	@SuppressWarnings("unused")
	private static final String TAG = "ProgressManagerEx";
	private UpdateProcess updateProcess;
	private long lastUpdate;
	
	public ProgressManagerAds(Context context){
		super(context);
	}

	public void setUpdateProcess(UpdateProcess updateProcess){
		this.updateProcess = updateProcess;
	}
	
	@Override
	public void onUpdate(ProgressValue value)
	{
		UpdateStatus status = value.status;
		int progress = value.progress;
		SimpleMessageBox simpleMessageBox = value.simpleMessageBox;
		
		long now = SystemClock.uptimeMillis();
		if( status == UpdateStatus.STEP && now - lastUpdate < REFRESH_INTERVAL )
			return;
		lastUpdate = now;
		
		updateStatus(status, progress, simpleMessageBox);
	}
	
	@Override
	protected void createProgressDialog(int title, int message) {
		super.createProgressDialog(title, message);
		progressDialog.setOnCancelListener(this);
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		updateProcess.cancel(false);
	}
}