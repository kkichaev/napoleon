package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import java.util.List;


public class ScriptImplEx extends ScriptImpl {
	
	public long calcDocSum() {
		long curSum = 0;
		
		CreatableDocument<?>[] docs = getDocuments();
		for(CreatableDocument<?> doc : docs) {
			if( doc == null )
				continue;
			
			long ds = doc.sum();
			if( OrderImplBase.class.isAssignableFrom(doc.getClass()) && ((OrderImplBase<?>)doc).useDocSumInscriptSum()){
				curSum += ds;
			} 
		}
		
		return curSum;
	}
	
	public boolean isComplete() {
		boolean result = super.isComplete();
		
		for(ScriptItem item : data.items){
			if (item.type.equals(IncassDoc.instance().getObjectName())){
				IncassImpl vi = (IncassImpl) IncassDoc.instance().create();
				
				if(vi.read(item.date.getTime())){
					vi.close();
					IncassEx v = (IncassEx) vi.getData();
					
					if(v.dover.trim().length() == 0){
						return false;
					}
				}
			}
			
			
		}
		
		return result;
	}

	@Override
	protected DocSendListner createDocSender(ScriptItem si, CreatableDocument<?> doc) {
		if(doc instanceof OrderImplEx) {
			return new DocSendListner(((OrderEx)doc.getData()).objName(), doc);
		}
		return super.createDocSender(si, doc);
	}
}
