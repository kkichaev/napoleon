package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.dataobjects.WSales;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.util.ProgressManager;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class RcvWSales extends NetworkAsyncTask{
	protected UserInfo userInfo;
	protected static Lock lock = new ReentrantLock();
	protected Context context;
	private String barcode = "";

	public interface TaskDoneHandler {
		void start();
		void finish(NetworkAsyncTask task);
	}
	
	public RcvWSales(Activity context, String barcode) {
		super(new ProgressManager(context));
		((ProgressManager)this.progressHelper).setUpdateProcess(this);
		this.context = context;
		this.barcode = barcode;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean ret = false;
		if( !lock.tryLock() )
			return ret;
		try {
			String errMessage = null;
			
			onUpdate(UpdateStatus.BEGIN_UPDATE, 0);
			
			Config config = ConfigManager.getConfig();
			userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			h.add(new HitchOnSelect(WSales.class, "WSales", String.format("barcode='%s'", barcode)));
			
			ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(h);
			dataBaseUpdater.setUpdateProcessListenet(this);
			if( !dataBaseUpdater.update(context, userInfo, false) )
				errMessage = dataBaseUpdater.getMessage();
			
			if (!isCancelled()) {
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
				if( errMessage != null ) {
					showErrorMsg(errMessage, progressHelper.getContext());
				} else {
					Activity a = (Activity) progressHelper.getContext();
					a.runOnUiThread(new Runnable() {
						@Override public void run() { 
							Toast.makeText(progressHelper.getContext(), R.string.remains_data_updated, Toast.LENGTH_SHORT).show(); 
						}
					});
					ret = true;
				}
			}
			
			return ret;
		} catch(Exception e) {
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();			
			if (dataBase.isDbLockedByCurrentThread() || dataBase.isDbLockedByOtherThreads())
				dataBase.endTransaction();
			
			if (!isCancelled()) {
				String message = e.getMessage();
				if( message == null )
					message = context.getString(R.string.recieved_error);
				showErrorMsg(message, progressHelper.getContext());
			}
			
			e.printStackTrace();
			
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	

	@Override
	public void onUpdate(UpdateStatus status, int progress) {
	}
}
