package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportAnswerHitching;
import com.grsoft.database.ReportRequestHitching;
import com.grsoft.dataobjects.ReportList;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.util.DataSetNotify;
import com.grsoft.view.TimerMessageBox;

import android.content.Context;
import android.view.View;

public class ReportSync extends NetworkAsyncTask {
	private View control;
	protected Context context;
	
	public static Hitching ReportListHitching = new RcvNewHitching(ReportList.class);

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

			ObjectExportListener req = new ReportRequestHitching();
			if( req.size() > 0 ) {
				ArrayList<ObjectListener> docs = new ArrayList<ObjectListener>();
				docs.add(req);
				
				WriteServiceBase writer = (WriteServiceBase) RWServiceFactory.instance.createWriteService(docs, false);
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
			rcvHitch.add(ReportListHitching);
			rcvHitch.add(new ReportAnswerHitching());
		
			ReadServiceBase reader =  (ReadServiceBase) RWServiceFactory.instance.createReadService(rcvHitch);
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
