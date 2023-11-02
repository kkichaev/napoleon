package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

public class ScriptImplEx extends ScriptImpl {
	public long calcDocSum() {
		long curSum = 0;
		
		CreatableDocument<?>[] docs = getDocuments();
		for(CreatableDocument<?> doc : docs) {
			if( doc == null )
				continue;
			
			long ds = doc.sum();
			if( doc instanceof IncassImpl || doc instanceof IncassDebDistrImpl)
				curSum += ds;
		}
		
		return curSum;
	}
}
