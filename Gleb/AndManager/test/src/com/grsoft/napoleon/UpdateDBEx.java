package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
//import com.grsoft.dataobjects.RepData;
//import com.grsoft.dataobjects.RepParam;
import com.grsoft.dataobjects.ReportOnAgentForDateData;
import com.grsoft.dataobjects.ReportOnAgentForDateParam;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.view.SimpleMessageBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		
		ret.clear();
		
		ReportOnAgentForDateParam rp = new ReportOnAgentForDateParam();
		
		rp.date = Util.getDate();
		
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(new RcvNewHitching(ReportOnAgentForDateData.class, "TypeName"));
		
		ret.add(new ReportHitching("ReportOnAgentForDate", rp, result));
		
		return ret;
	}
	
	@Override
	protected UpdateProcess getUpdateProcess() {
		return new UpdEx(this);
	}
	
	class UpdEx extends UpdateProcess {

		public UpdEx(Activity context) {
			super(context);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if(!lock.tryLock()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, Html.fromHtml(activity.getString(R.string.use_sync_later)), Toast.LENGTH_LONG)
								.show();
						
					}
				});
				return false;
			}

			boolean ret = true;
			try {
				String errMessage = null;
				
				List<Hitching> rcvHitch = getGenDataHitchings();
				if( rcvHitch.size() > 0 ) {
					ReadService dataBaseUpdater =  (ReadService) new ReadService(rcvHitch, true, activity);
					dataBaseUpdater.setUpdateProcessListenet(this);
					
					FoldersAdapter.resetCache();
					
					if( !dataBaseUpdater.update(activity, getRcvUserInfo(), false) ){
						errMessage = dataBaseUpdater.getMessage();
					}else{
						traffic += dataBaseUpdater.getReceivedBytes();
					}
				}
			
				if (!isCancelled())
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
				if (!isCancelled()){
					if( errMessage != null ) {
						showErrorMsg(errMessage, activity);
						return false;
					} else {
						onFinishUpdate();
						
						SimpleMessageBox smb = new SimpleMessageBox(getString(R.string.inform), 
								getString(R.string.sync_end_traffic) + 
								Integer.toString((traffic + 512) / 1024) + " " + 
								getString(R.string.kB), activity); 
						onUpdateMessage(smb);
						Thread.sleep(3000);
						smb.hide();
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
					message = activity.getString(R.string.recieved_error);
				if (!isCancelled())
					showErrorMsg(message, activity);
				
				exception.printStackTrace();
				
				ret = false;
			}
			finally {
				enableControlButton(true);
				lock.unlock();
			}
			return ret;
		}
		
	}
}
