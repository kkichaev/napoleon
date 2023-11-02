package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.database.VisitItemHitching;
import com.grsoft.database.DataObjectSendHitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PhotoListDoc;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;

public class VisitSendHelper {
	
	int traffic = 0;
	SocketConnection activeConn = null;
	String error = null;
	
	public int getTraffic() { return traffic; }
	public SocketConnection getActiveConnect() { return activeConn; }
	public void setActiveConnnection(SocketConnection newac) { activeConn = newac; }
	public String getError() { return error; }
	
	public boolean send(Context context, UserInfo userInfo, List<CreateDocDataObject> docs, UpdateProcessListener listener) {
		boolean ret = true;
		
		if(docs.size() == 0)
			return true;
		
		DbWriter w = new DbWriter();
		
		WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(null);
		writeService.setCloseConnection(false);

//		writeService.setUpdateProcessListenet(listener);

		List<ObjectExportListener> toSend = new ArrayList<ObjectExportListener>();
		
		traffic = 0;
		
		int count = 0;
		for(CreateDocDataObject doc : docs)
			count += ((PhotoListDoc)doc).getItems().size();

		if(listener != null)
			listener.onUpdate(UpdateStatus.BEGIN_SEND_VISITS, count);
				
		count = 0;
		for(CreateDocDataObject doc : docs) {
			writeService.setActiveConnection(activeConn);
			PhotoListDoc pd = (PhotoListDoc)doc;
			
			DataObjectSendHitching vhs = new DataObjectSendHitching(doc, pd.getDocName());
			toSend.add(vhs);
			
			List<VisitItem> items = pd.getItems();
			pd.setItems(new ArrayList<VisitItem>());

			int idx = 0;
			do {
				if(idx < items.size())
					toSend.add(new VisitItemHitching((CreateDocDataObject)doc, idx, items, pd.getItemName()));

				
				if(!writeService.forcePut(context, userInfo, toSend, activeConn, false)) {
					ret = false;
					error = writeService.getMessage();
					break;
				}
				activeConn = writeService.getActiveConnection();
				
				if(listener != null)
					listener.onUpdate(UpdateStatus.STEP, ++count);
				
				
				toSend.clear();
				idx++;
			} while(idx < items.size());
			
			if(!ret)
				break;
			doc.params |= ParamState.ofExported;
			pd.setItems(items);
			w.insertRecord(doc);
		}
		traffic += writeService.getSendedBytes();
		if(listener != null)
			listener.onUpdate(UpdateStatus.END, ++count);
		
		w.close();
		try {
			writeService.sendByeCommanToCloseSession(userInfo, activeConn, context);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		writeService.closeConnection();
		return ret;
	}
}
