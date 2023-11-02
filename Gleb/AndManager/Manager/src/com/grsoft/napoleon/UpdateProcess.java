package com.grsoft.napoleon;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.manager.R;
import com.grsoft.napoleon.manager.UpdateProcessOwner;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.util.ProgressManager;

public class UpdateProcess extends NetworkAsyncTask {
	
//		private final static String TAG = "UpdatePeriodProcess";
		protected int traffic = 0;
		protected Activity mActivity;		
		protected UpdateProcessOwner mProcessOwner;
		protected static Lock lock = new ReentrantLock();
		List<Hitching> request;
		
		public UpdateProcess(Activity context, UpdateProcessOwner processOwner, List<Hitching> request) {
			
			super(new ProgressManager(context));
			
			((ProgressManager)this.progressHelper).setUpdateProcess(this);
			
			this.request = request;
			
			mActivity = context;
			mProcessOwner = processOwner;
		}
		
		
		protected UpdateProcess(ProgressHelper progressHelper){
			super(progressHelper);
		}
		
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if(!lock.tryLock()) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity, 
								Html.fromHtml(mActivity.getString(R.string.use_sync_later)), Toast.LENGTH_LONG)
								.show();
						
					}
				});
				
				return false;
			}

			boolean ret = true;
			try {
				
				mProcessOwner.enableControlButton(false);
				
				String errMessage = null;
				
				if( request.size() > 0 ) {
					ReadServiceBase reader =  new ReadServiceBase(request, true, mActivity);
					reader.setUpdateProcessListenet(this);
					
					if( !reader.update(mActivity, getRcvUserInfo(), false) ){
						errMessage = reader.getMessage();
					}else{
						traffic += reader.getReceivedBytes();
					}
				}
			
				if (!isCancelled())
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
				if (!isCancelled()){
					if( errMessage != null ) {
						showErrorMsg(errMessage, mActivity);
						return false;
					} else {
						mProcessOwner.onFinish();						
//						SimpleMessageBox smb = new SimpleMessageBox(mActivity.getString(R.string.inform), 
//								mActivity.getString(R.string.sync_end_traffic) + 
//								Integer.toString((traffic + 512) / 1024) + " " + 
//								mActivity.getString(R.string.kB), mActivity); 
//						onUpdateMessage(smb);
//						Thread.sleep(3000);
//						smb.hide();
					}
				}
				
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
				mProcessOwner.enableControlButton(true);
				lock.unlock();
			}
			return ret;
		}
		
		protected UserInfo getRcvUserInfo(){
			Config config = ConfigManager.getConfig();
			return new LoginData(config.login, config.passw, config.impersonate, mActivity);
		}	
}		
		
