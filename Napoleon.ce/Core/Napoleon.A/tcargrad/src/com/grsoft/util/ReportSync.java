package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.GetReportsHitching;
import com.grsoft.dataobjects.ReportDef;
import com.grsoft.dataobjects.ReportsRequestHitching;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.TimerMessageBox;

public class ReportSync extends NetworkAsyncTask {
	private View control;
	protected Context context;

	public ReportSync(Context context, View control) {
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

			ObjectExportListener req = new ReportsRequestHitching();
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
			
			List<Hitching> rcvHitch = new ArrayList<Hitching>();
			rcvHitch.add(new RcvNewHitching(ReportDef.class, "ReportDef"));
			rcvHitch.add(new GetReportsHitching());
		
			ReadService reader =  (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
			reader.setUpdateProcessListenet(this);
			if( !reader.update(context, userInfo, false) ){
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				showErrorMsg(reader.getMessage(), context);
				
				return false;
			}else{
				traffic += reader.getReceivedBytes();
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
