package com.grsoft.napoleon.documents;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.grsoft.database.DbReader;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.PicStoreHitching;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.PhotoListDoc;
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
import com.grsoft.network.UserInfo;
import com.grsoft.network.VisitSendHelper;
import com.grsoft.network.WriteService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.TimerMessageBox;

public class DocumentSender extends NetworkAsyncTask {
	private View control;
	protected Context context;
	private SendResultListener sendResultListener;
	private List<DocExportListener> docList = new ArrayList<DocExportListener>();
	
	int traffic = 0;
	
	public DocumentSender(Context context, View control, 
			String objName, CreatableDocument<?> document, long rid){
		this(context, control, objName, document, rid, null);
	}
	
	public DocumentSender(Context context, View control, List<DocExportListener> documents) {
		this(context, control, (SendResultListener)null);
	
		docList.addAll(documents);
	}

	public DocumentSender(Context context, View control, List<DocExportListener> documents, SendResultListener sendResultListener) {
		this(context, control, sendResultListener);
	
		docList.addAll(documents);
	}

	public DocumentSender(Context context, View control, 
			String objName, CreatableDocument<?> document, long rid, 
			SendResultListener sendResultListener){
		this(context, control, sendResultListener);

		docList.add(new DocSendListner(objName, document, rid));
	}
	
	public DocumentSender(Context context, View control, 
			DocExportListener docSend, 
			SendResultListener sendResultListener) {
		this(context, control, sendResultListener);
		docList.add(docSend);
	}
	
	public DocumentSender(Context context, View control, SendResultListener sendResultListener){
		super(new SendProgressManager(context, control));
		this.control = control;
		this.context = context;
		this.sendResultListener = sendResultListener;
	}
	
	List<CreateDocDataObject> splitPhotoDocs() {		
		List<CreateDocDataObject> ret = new ArrayList<CreateDocDataObject>();
		if(Features.UNLIMIT_VISIT_ITEMS) {
			DbReader r = new DbReader();
//			Class<?> visClass = DbObject.getDataType(Visit.class);

			List<DocExportListener> rmvDl = new ArrayList<DocExportListener>();
			
			for(DocExportListener del : docList) {
				List<Long> rmvDoc = new ArrayList<Long>();
				DocList dl = del.getDocuments();
				if(dl == null) continue;
				for(Document<?> d : dl) {
					if(d.getData() instanceof PhotoListDoc) {
						rmvDoc.add(d.getRowid());
						try {
							CreateDocDataObject v = (CreateDocDataObject) d.getData().getClass().newInstance();
							if( r.read(v, v.getTableName(), d.getRowid()) ) {
								ret.add(v);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				dl.removeDocuments(rmvDoc);
				if(dl.getCount() == 0) {
					rmvDl.add(del);
				}
			}
			
			r.close();
			docList.removeAll(rmvDl);
		}
		return ret;
	}
	
	@Override
	protected Boolean doInBackground(Void... params)
	{
		if (isDocListEmpty())
			return true;
		
		if (Features.CANT_RESEND_SENDED_DOCUMENT)
			for(DocExportListener del : docList)
				for(Document<?> d : del.getDocuments())
					if (d instanceof CreatableDocument<?> && ((CreatableDocument<?>)d).isExported()){
						showErrorMsg("Документ отправлен на сервер", context);
						return true;
					}
		
		onUpdate(UpdateStatus.START_OF_PROCESS, 0);

		try	{
			Config config = ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			
			List<CreateDocDataObject> vdocs = splitPhotoDocs();
			
			List<ObjectListener> docs = new ArrayList<ObjectListener>(docList);
			GPSHitching gps = new GPSHitching();
			if( gps.size() > 0 )
				docs.add(gps);
			
			LogHitching logHitching = new LogHitching();			
			if (logHitching.needUpdate())
				docs.add(logHitching);
			
			PotenzialOrgHitching poh = createPotenzialOrgHitching();
			if(poh != null && poh.size() > 0)
				docs.add(poh);
			
			PicStoreHitching psh = new PicStoreHitching();
			
			if (psh != null && psh.size() > 0)
				docs.add(psh);
			
			WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs, ConfigHelper.isRcvRemnants());
			writeService.setUpdateProcessListenet(this);
			writeService.setCloseConnection(false);
			
			if (docs.size() > 0 && !writeService.write(context, userInfo)){
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				showErrorMsg(writeService.getMessage(), context);
				
				return false;
			}
			else{
				traffic += writeService.getSendedBytes();
				
				if(vdocs.size() > 0) {
					VisitSendHelper vsh = new VisitSendHelper();
					vsh.setActiveConnnection(writeService.getActiveConnection());
					if( !vsh.send(context, userInfo, vdocs, this) ) {
						showErrorMsg(vsh.getError(), context);
					} else {
						traffic += vsh.getTraffic();
					}
				} else {
					writeService.sendByeCommanToCloseSession(userInfo, writeService.getActiveConnection(), context);
				}
				
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				onUpdateMessage(new TimerMessageBox(
						context.getString(R.string.result), context.getString(R.string.sync_end_traffic)
						+ Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB)
						, context));

				writeService.closeConnection();
				return true;
			}
		} catch(Exception exception){
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			showErrorMsg(exception.getMessage(), context);
			exception.printStackTrace();
			
			return false;
		} 
	}

	protected PotenzialOrgHitching createPotenzialOrgHitching() { return new PotenzialOrgHitching(); }

	protected boolean isDocListEmpty() {
		int count = 0;
		for(DocExportListener del : docList) {
			count += del.getDocuments().getCount();
		}
		return count == 0;
	}
	
	protected Collection<ObjectListener> getObjectsToSend() {
		return new ArrayList<ObjectListener>();
	}

	@Override
	protected void onPreExecute() {
		if(control != null && context instanceof Activity) {
			((Activity)context).runOnUiThread(new Runnable() {
				@Override public void run() { control.setEnabled(false); }
			});
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for(DocExportListener del : docList )
			del.getDocuments().close();

		showRecievedMessage(null);
		
		if(control != null && context instanceof Activity) {
			((Activity)context).runOnUiThread(new Runnable() {
				@Override public void run() { control.setEnabled(true); }
			});
		}
		
		fireSendResult(result);
		
	}

	private void fireSendResult(Boolean result) {
		if (sendResultListener != null)
			sendResultListener.postSendExecute(result);
	}
	
	public void setSendResultListener(SendResultListener listener) {
		this.sendResultListener = listener;
	}
}
