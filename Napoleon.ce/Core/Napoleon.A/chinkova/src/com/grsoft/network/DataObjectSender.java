package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.TimerMessageBox;


public class DataObjectSender extends NetworkAsyncTask {
	private int traffic = 0;
	private Activity activity;
	private List<ObjectExportListener> hitchings;
	
	public DataObjectSender(Activity activity, int controlid, ObjectExportListener hitching) {
		super(new SendProgressManager(activity, activity.findViewById(controlid)));
		this.hitchings = new ArrayList<ObjectExportListener>();
		if(hitching.size() > 0)
			this.hitchings.add(hitching);
		this.activity = activity;
	}

	public DataObjectSender(Activity activity, int controlid, List<ObjectExportListener> h) {
		super(new SendProgressManager(activity, activity.findViewById(controlid)));
		this.hitchings = new ArrayList<ObjectExportListener>();
		for(ObjectExportListener oel : h)
			if(oel.size() > 0)
				this.hitchings.add(oel);
		this.activity = activity;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (hitchings.size() == 0)
			return true;
		
		onUpdate(UpdateStatus.START_OF_PROCESS, 0);

		try	{
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, activity);
			List<ObjectExportListener> export = new ArrayList<ObjectExportListener>();
			export.addAll(hitchings);
			WriteService writeService = (WriteService) RWServiceFactory
					.instance.createWriteService(export);
			writeService.setUpdateProcessListenet(this);
			
			if (!writeService.write(activity, userInfo)){
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				showErrorMsg(writeService.getMessage(), activity);
				
				return false;
			}
			else{
				traffic += writeService.getSendedBytes();
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				onUpdateMessage(new TimerMessageBox(activity.getString(R.string.inform), 
						activity.getString(R.string.sync_end_traffic) + 
					Integer.toString((traffic + 512) / 1024) + activity.getString(R.string.kB), 
					activity));
				
				return true;
			}
		} catch(Exception exception){
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			showErrorMsg(exception.getMessage(), activity);
			exception.printStackTrace();
			
			return false;
		} 
	}
}
