package com.grsoft.network.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RawObject;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.exception.RuntimeException;

public class ReceiveVisit extends NetworkAsyncTask {

	protected UserInfo userInfo;
	protected static Lock lock = new ReentrantLock();
	private VisitImpl visit;
	private TaskDoneHandler doneHandler;
	private Context context;

	public interface TaskDoneHandler {
		void taskDone(NetworkAsyncTask task);
	}
	
	public ReceiveVisit(Activity context, TaskDoneHandler doneHandler, VisitImpl visit) {
		super(new ProgressManager(context));
		((ProgressManager)this.progressHelper).setUpdateProcess(this);
		
		this.doneHandler = doneHandler;
		this.visit = visit;
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
			userInfo = new LoginData(config.login, config.passw, context);
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			h.add(new VisitHitching(visit));
			
			ReadServiceBase dataBaseUpdater =  RWServiceFactory.instance.createReadService(h);
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
							Toast.makeText(progressHelper.getContext(), "Данные по посещению обновлены", 
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
					message = "Ошибка при приеме";
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

		super.onPostExecute(result);
	}

}

class VisitHitching extends HitchOnSelect{
	private VisitImpl visit;
	public VisitHitching(VisitImpl visit) {
		super(Visit.class, "Visit");
		this.visit = visit;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		setCondition(String.format(" userid = '$CURRENT_USERID' and created = ToDate('%s')", 
				sdf.format(visit.getData().created)));
}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Visit rcvVisit = (Visit)rawObject.createDataObject(Visit.class);
		
		if (rcvVisit != null && 
				rcvVisit.items != null && 
				rcvVisit.items.size() > 0){
			
			visit.deleteSrcItems();
			
			for(int i = 0; i < rcvVisit.items.size(); i++){
				String fileName = String.format("imported%d_%d.jpg", visit.getData().created.getTime(), i);
				
				File file = new File(Path.getDataDir(), fileName); 
				
				if (file.exists())
					file.delete();
				
				File path = new File(Path.getDataDir());
				path.mkdirs();

				try{
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(rcvVisit.items.get(i).id);
					fos.close();
				
					visit.addPhoto(file.getAbsolutePath().toString().getBytes());
					Thread.sleep(3000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
}
