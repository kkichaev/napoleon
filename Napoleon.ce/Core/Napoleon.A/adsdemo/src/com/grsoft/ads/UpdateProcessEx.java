package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.grsoft.ads.database.CertificateHitching;
import com.grsoft.ads.database.CounterHitching;
import com.grsoft.ads.database.ProtocolHitching;
import com.grsoft.ads.database.WorkTypeHitching;
import com.grsoft.ads.dataobjects.impl.CertificateImpl;
import com.grsoft.ads.dataobjects.impl.OrderExtended;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.dataobjects.impl.ProtocolImpl;
import com.grsoft.ads.dataobjects.impl.UserOrderImpl;
import com.grsoft.ads.utils.LockOwner;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateProcessEx extends UpdateProcess {
	public UpdateProcessEx(Activity activity, LockOwner lockOwner){
		super(activity, lockOwner);
	}
	
	@Override
	protected void onPostExecuteWork(Boolean result) {
		super.onPostExecuteWork(result);
		
		if (result){
			new OrderImpl().removeOldDocuments();
			new UserOrderImpl().removeOldDocuments();
		}
	}
	
	@Override
	protected List<Hitching> createImportData() {
		List<Hitching> result = super.createImportData();
		
		if (result == null)
			result = new ArrayList<Hitching>();
		
		result.add(new CounterHitching());
		result.add(new WorkTypeHitching());
		result.add(new ProtocolHitching());
		result.add(new CertificateHitching());
		
		return result;
	}
	
	@Override
	protected List<ObjectListener> createExportData() {
		List<ObjectListener> result = super.createExportData();
		List<ObjectListener> setwriteoflist = new ArrayList<ObjectListener>();
		
		if (result != null)
			for(ObjectListener ol : result){
				String name = ol.getObjectName(); 
				if (name.equals("Order") || name.equals("UserOrder"))
					setwriteoflist.addAll(greateDocWriteof(ol));
			}
		
		if (setwriteoflist.size() > 0)
			result.addAll(setwriteoflist);
		
		return result;
	}

	private List<ObjectListener> greateDocWriteof(final ObjectListener ol) {
		ArrayList<ObjectListener> result = new ArrayList<ObjectListener>();
		
		if (ol instanceof DocSendListner){
			
			CertificateImpl certImpl = new CertificateImpl();
			ProtocolImpl prtImpl = new ProtocolImpl();
			ArrayList<Long> crtIds = new ArrayList<Long>();
			ArrayList<Long> prtIds = new ArrayList<Long>();
			
			com.grsoft.napoleon.documents.DocList list = ((DocSendListner)ol).getDocuments();
			
			for(int i = 0; i < list.getCount(); i++){
				Document<?> doc = list.get(i);
				
				if(doc instanceof OrderExtended){
					String cert = ((OrderExtended)doc).getCertificate();
					
					if (cert != null && cert.length()>0){
						certImpl.getData().number = cert;
					
						if (certImpl.read())
							crtIds.add(certImpl.getRowid());
					}
					
					String prt = ((OrderExtended)doc).getProtocol();
					
					if(prt != null && prt.length() > 0){
						prtImpl.getData().number = prt;
						
						if (prtImpl.read())
							prtIds.add(prtImpl.getRowid());
					}
						
				}
			}
			
			if (crtIds.size() > 0){
				Long[] ids = new Long[crtIds.size()]; 
				result.add(new WriteofDoc(new CertificateImpl(), 
						"CertificateUsed", crtIds.toArray(ids)));
			}
			
			if (prtIds.size() > 0){
				Long[] ids = new Long[prtIds.size()];
				result.add(new WriteofDoc(new ProtocolImpl(), 
						"ProtocolUsed", prtIds.toArray(ids)));
			}
			
			certImpl.close();
			prtImpl.close();
		}
		
		return result; 
	}
}

class WriteofDoc implements ObjectExportListener{
	String objectName;
	Long[] ids;
	DbObject<? extends DataObject> dbObject;
	
	public WriteofDoc(DbObject<? extends DataObject> dbObject,
			String objectName, Long[] ids) {
		this.objectName = objectName;
		this.ids = ids;
		this.dbObject = dbObject;
	}
	
	@Override
	public void onStart() {}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {}

	@Override
	public void onSave() {}

	@Override
	public void onEnd() {
		dbObject.close();
	}

	@Override
	public String getObjectName() {	return objectName; }

	@Override
	public int size() {	return ids.length; }

	@Override
	public DataObject get(int i) {
		dbObject.read(ids[i]);
		return dbObject.getData();
	}
	
}
