package com.grsoft.network.util;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;

public class ReceiveRemnants extends NetworkAsyncTask {

	protected UserInfo userInfo;
	protected static Lock lock = new ReentrantLock();
	protected TaskDoneHandler doneHandler;
	protected Context context;

	public interface TaskDoneHandler {
		void start();
		void finish(NetworkAsyncTask task);
	}
	
	public ReceiveRemnants(Activity context, TaskDoneHandler doneHandler) {
		super(new ProgressManager(context));
		((ProgressManager)this.progressHelper).setUpdateProcess(this);
		
		this.doneHandler = doneHandler;
		this.context = context;
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
			userInfo = new LoginData(config.login, config.passw, config.impersonate, context
				, config.uuid, config.serverCode);
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			h.add(new RemnantsHitching());
			
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
	protected void onPostExecute(Boolean result) {
		if( doneHandler != null )
			doneHandler.finish(this);

		super.onPostExecute(result);
	}

	@Override
	public void onUpdate(UpdateStatus status, int progress) {
	}
	
	@Override
	protected void onPreExecute() {
		if( doneHandler != null )
			doneHandler.start();
	}
}
