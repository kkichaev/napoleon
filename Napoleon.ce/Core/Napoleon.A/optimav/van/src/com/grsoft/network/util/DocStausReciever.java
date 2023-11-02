package com.grsoft.network.util;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HandledDocumentsHitching;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.HandledDocuments;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;

public class DocStausReciever extends NetworkAsyncTask {

	protected UserInfo userInfo;
	protected static Lock lock = new ReentrantLock();
	TaskDoneHandler doneHandler;
	Context context;

	public interface TaskDoneHandler {
		void taskDone(NetworkAsyncTask task);
	}
	
	public DocStausReciever(Activity context, TaskDoneHandler doneHandler) {
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
			userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			h.add(new HandledDocumentsHitching());
			
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
							Toast.makeText(progressHelper.getContext(), R.string.doc_status_updated, 
									Toast.LENGTH_SHORT).show(); 
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
			doneHandler.taskDone(this);
		HandledDocuments.clearCache();
		super.onPostExecute(result);
	}

	@Override
	public void onUpdate(UpdateStatus status, int progress) {
		// TODO Auto-generated method stub
	}

}
