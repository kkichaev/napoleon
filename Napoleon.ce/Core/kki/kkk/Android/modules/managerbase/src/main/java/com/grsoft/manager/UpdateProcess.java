package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DivisionManager;
import com.grsoft.dataobjects.impl.DivisionManagerImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.util.Consts;
import com.grsoft.util.gps.GPSUtilNew;

public class UpdateProcess extends NetworkAsyncTask {
		protected int traffic = 0;
		protected Activity mActivity;		
		protected UpdateCtrl mProcessOwner;
//		protected static Lock lock = new ReentrantLock();
		List<Hitching> request;
		List<ObjectListener> sending;
		boolean ret;
		
		public UpdateProcess(Activity context, UpdateCtrl processOwner, List<Hitching> request) {
			
			super(new ProgressManager(context));
			
			((ProgressManager)this.progressHelper).setUpdateProcess(this);
			
			this.request = request;
			this.request.add(0, new Hitching(DivisionManager.class, "IAMDivisionManager"));
			mActivity = context;
			mProcessOwner = processOwner;
		}
		
		
		protected UpdateProcess(ProgressHelper progressHelper){
			super(progressHelper);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
//			if(!lock.tryLock()) {
//				mActivity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(mActivity, 
//								Html.fromHtml(mActivity.getString(R.string.use_sync_later)), Toast.LENGTH_LONG)
//								.show();
//					}
//				});
//				
//				return false;
//			}
			
//			LicenseManager lm = new LicenseManager();
//			final String login = ProgData.getLogin();
//			final String project = ProgData.getProject(mActivity);
//			if( !lm.canConnect(mActivity, project, login) ) {
//				showErrorMsg(lm.getError(), mActivity);
//				return false;
//			}

			ret = true;
			String errMessage = null;
			try {
				ManagerApplicationBase.putSyncTime(mActivity, (new Date()).getTime());
				
				mActivity.runOnUiThread(new Runnable() { @Override public void run() { mProcessOwner.updateCtrl(false);	} });
				

				GPSHitching h = new GPSHitching();
				if(h.size() > 0) {
					if(sending == null) {
						sending = new ArrayList<ObjectListener>();
					}
					sending.add(h);
				}
				
				if ( sending != null && sending.size() > 0){
					WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(sending);
					writeService.setUpdateProcessListenet(this);
		
					if (!writeService.write(mActivity, getRcvUserInfo())) 
						errMessage = writeService.getMessage();
					else 
						traffic += writeService.getSendedBytes();
				}
			
				if( errMessage == null && request != null && request.size() > 0 ) {
					ReadServiceBase reader =  new ReadServiceBase(request, true, mActivity);
					reader.setUpdateProcessListenet(this);
					
					if( !reader.update(mActivity, getRcvUserInfo(), false) )
						errMessage = reader.getMessage();
					else
						traffic += reader.getReceivedBytes();
				}
				
				if (!isCancelled())
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
//				if (!isCancelled()){
//					if( errMessage == null ) {
//						Logger lg = LicenseManager.getLogger(project, login, mActivity.getApplicationContext());
//						lg.addSuccessConnect(new Date());
//					}
//				}
				
			}
			catch(Exception exception) {
				SQLiteDatabase dataBase = DataBaseManager.getDataBase();
				
				if (dataBase.isDbLockedByCurrentThread() || dataBase.isDbLockedByOtherThreads()) {
					try {
						dataBase.endTransaction();							
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				String message = exception.getMessage();
				if( message == null )
					message = mActivity.getString(R.string.recieved_error);
				if (!isCancelled())
					showErrorMsg(message, mActivity);
				
				exception.printStackTrace();
				
				ret = false;
			}
			finally {
				if(errMessage != null) {
					ret = false;
					if( !isCancelled() )
						showErrorMsg(errMessage, mActivity);
				}
				mActivity.runOnUiThread(new Runnable() { @Override	public void run() {	
					
					if(DivisionManagerImpl.isMobile())
						GPSUtilNew.start(mActivity, Consts.ONE_SECOND * 60, 100);
					
					mProcessOwner.onFinish(ret);
					mProcessOwner.updateCtrl(true);	
			}});
//				lock.unlock();
			}
			return ret;
		}
		
		protected UserInfo getRcvUserInfo(){
			Config config = ConfigManager.getConfig();
			return new LoginData(config.login, config.passw, config.impersonate, mActivity);
		}	
		
		@Override
		protected void onCancelled() { super.onCancelled();	}
		
		public void setSending(List<ObjectListener> toSend){
			this.sending = toSend;
		}
}		
		
