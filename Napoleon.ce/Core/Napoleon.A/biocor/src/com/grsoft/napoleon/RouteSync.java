package com.grsoft.napoleon;

import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import com.grsoft.database.OrgFoldersHitching;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.util.DataSetNotify;
import com.grsoft.view.TimerMessageBox;


public class RouteSync extends NetworkAsyncTask {
	private View control;
	protected Context context;

	public RouteSync(Context context, View control) {
		super(new SendProgressManager(context, control));
		this.control = control;
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		onUpdate(UpdateStatus.START_OF_PROCESS, 0);

		int traffic = 0;
		
		try	{
			Config config = ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, context);

			ObjectExportListener req = new OrgFoldersHitching();
			if( req.size() > 0 ) {
				ArrayList<ObjectListener> docs = new ArrayList<ObjectListener>();
				docs.add(req);
				
				WriteService writer = (WriteService) RWServiceFactory.instance.createWriteService(docs, false);
				writer.setUpdateProcessListenet(this);
				if (!writer.write(context, userInfo)){
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					showErrorMsg(writer.getMessage(), context);
					
					return false;
				}
				else {
					traffic += writer.getSendedBytes();
				}
			}
			
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			onUpdateMessage(new TimerMessageBox(
					context.getString(R.string.inform), context.getString(R.string.sync_end_traffic)
					+ Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB)
					, context));
			
		} catch(Exception exception){
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			showErrorMsg(exception.getMessage(), context);
			exception.printStackTrace();
			
			return false;
		} 

		return true;
	}

	@Override
	protected void onPreExecute() {
		if (control != null)
			control.setEnabled(false);
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (control != null)
			control.setEnabled(true);
		if( context instanceof DataSetNotify)
			((DataSetNotify)context).notifyDataSetChanged();
	}
}
