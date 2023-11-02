package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.ProgID;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.TimerMessageBox;

import android.content.Context;

public class SendData extends NetworkAsyncTask {
	public interface Handler {
		void onSend(boolean result);
	}
	
	Context context;
	DocExportListener documents;
	Handler handler;
	
	public SendData(Context context, DocExportListener documents, Handler handler) {
		super(new SendProgressManager(context, null));
	
		this.context = context;
		this.documents = documents;
		this.handler = handler;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		boolean result = true;
		int traffic = 0;
		String errMsg = "";
		
		if( documents.getDocuments().getCount() > 0 ) {
			onUpdate(UpdateStatus.START_OF_PROCESS, 0);
	
			try	{
				ServerCommand.DeviceID = ProgID.getPrgID(context);
				
				List<DocExportListener> docs = new ArrayList<DocExportListener>();
				docs.add(documents);
				
				WriteServiceBase writeService = new WriteService(docs, false);
				writeService.setUpdateProcessListenet(this);
				
				if (!writeService.write(context, new LoginData("", "", "", context))) {
					errMsg = writeService.getMessage();
					result = false;
				} else {
					traffic += writeService.getSendedBytes();
				}
			} catch(Exception exception){
				errMsg = exception.getMessage();
				exception.printStackTrace();
				
				result = false;
			} 	
		}
		onUpdate(UpdateStatus.END_OF_PROCESS, 0);
		if(!result) {
			showErrorMsg(errMsg, context);
		} else {
			onUpdateMessage(new TimerMessageBox(
					context.getString(R.string.inform), context.getString(R.string.sync_end_traffic)
					+ Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB)
					, context));
		}
		if( handler != null )
			handler.onSend(result);
		return result;
	}

}
