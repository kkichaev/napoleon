package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


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
}
