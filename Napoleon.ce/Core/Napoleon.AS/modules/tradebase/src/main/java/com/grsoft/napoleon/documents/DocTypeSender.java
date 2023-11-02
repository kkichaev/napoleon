package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.View;
import com.grsoft.database.GPSHitching;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.VisitSendHelper;
import com.grsoft.network.WriteService;
import com.grsoft.view.TimerMessageBox;

/***
 * Отправка всех документов выбранного типа 
 * @author kkichaev
 */

public class DocTypeSender extends NetworkAsyncTask {
	private DocType doctype;
	private Context context;
	private int traffic = 0;
	private SendResultListener postSend;
	
	public DocTypeSender(Context context, View control, DocType doctype) {
		super(new SendProgressManager(context, control));
		this.doctype = doctype;
		this.context = context;
		
		if(context instanceof SendResultListener)
			postSend = (SendResultListener)context;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		onUpdate(UpdateStatus.START_OF_PROCESS, 0);
		boolean result = false;
		String errMsg = null;

		try	{
			Config config = ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, context
					, config.uuid, config.serverCode);
			
			List<ObjectListener> docs = new ArrayList<ObjectListener>();
			GPSHitching gps = new GPSHitching();
			if( gps.size() > 0 )
				docs.add(gps);
			
			LogHitching logHitching = new LogHitching();			
			if (logHitching.needUpdate())
				docs.add(logHitching);
			
			boolean sended = false;
			if(Features.UNLIMIT_VISIT_ITEMS) {
				 List<CreateDocDataObject> phDocs = doctype.getDirtyPhotos();
				 if(phDocs != null && phDocs.size() > 0) {
						sended = true;
						VisitSendHelper vsh = new VisitSendHelper();
						if( !vsh.send(context, userInfo, phDocs, this) ) {
							errMsg = vsh.getError();
						} else {
							traffic += vsh.getTraffic();
						}
				 }				 
			}
			if(!sended) {
				DocExportListener d = doctype.getDirtyDocuments();
				if( d != null ) {
					if(d.getDocuments().getCount() > 0)
						docs.add(d);
					if(docs.size() != 0) {
						WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs, ConfigHelper.isRcvRemnants());
						writeService.setUpdateProcessListenet(this);
						
						if (!writeService.write(context, userInfo)){
							errMsg = writeService.getMessage();
						} else {
							traffic += writeService.getSendedBytes();
						}				
					}
				}
			}
//			while(true){
//				DocExportListener d = doctype.getDirtyDocuments();
//				if( d == null )
//					break;
//				
//				if(d.getDocuments().getCount() > 0)
//					docs.add(d);
//				
//				if(docs.size() == 0)
//					break;
//				
//				WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs, ConfigHelper.isRcvRemnants());
//				writeService.setUpdateProcessListenet(this);
//				
//				if (!writeService.write(context, userInfo)){
//					errMsg = writeService.getMessage();
//					break;
//				}
//				
//				traffic += writeService.getSendedBytes();
//				
//				docs.clear();
//			}
					
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			
			if(errMsg == null){
				onUpdateMessage(new TimerMessageBox(
						context.getString(R.string.result), context.getString(R.string.sync_end_traffic)
						+ Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB)
						, context));
				result = true;
			}else
				showErrorMsg(errMsg, context);
			
		} catch(Exception exception){
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			showErrorMsg(exception.getMessage(), context);
			exception.printStackTrace();
		} 
		
		return result;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		showRecievedMessage(null);
		
		postSend.postSendExecute(result);
	}

}
