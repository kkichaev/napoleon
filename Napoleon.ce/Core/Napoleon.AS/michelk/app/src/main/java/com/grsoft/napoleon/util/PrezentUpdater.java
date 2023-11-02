package com.grsoft.napoleon.util;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.database.UpdatePrezentHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.napoleon.R;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.util.ReceiveRemnants;


public class PrezentUpdater extends ReceiveRemnants {
	protected UserInfo userInfo;
	
	public PrezentUpdater(Activity context, TaskDoneHandler doneHandler) {
		super(context, doneHandler);
	}
	
	static class ReportParam extends DataObject{
		public List<Present>items = new ArrayList<Present>();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean ret = false;
		if( !lock.tryLock() )
			return ret;
		try {
			final ReportParam param = new ReportParam();
			PresentSdcard.init(context);
			
			DataTraveler.travel(PresentEx.class, new Travel<PresentEx>() {
				@Override
				public boolean travel(DataTraveler<PresentEx> item) {
					param.items.add(item.data);
					item.data = new PresentEx();
					return true;
				}}, null);
			
			String errMessage = null;
			
			onUpdate(UpdateStatus.BEGIN_UPDATE, 0);
			
			Config config = ConfigManager.getConfig();
			userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			
			List<Hitching> repResult = new ArrayList<Hitching>();
			repResult.add(new UpdatePrezentHitching());
			
			h.add(new ReportHitching("update_prezent", param, repResult));
			
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
							Toast.makeText(progressHelper.getContext(), R.string.update_prezent_done, Toast.LENGTH_SHORT).show(); 
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

}
