package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

public class ScriptImplEx extends ScriptImpl {
	@Override
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(ScriptDoc.OBJ_NAME, this));

		int index = 0;
		CreatableDocument<?>[] cd = getDocuments();
		for( ScriptItem si : data.items ) {
			CreatableDocument<?> d = cd[index]; 
			if( d != null) {
				docs.add(new DocSendListner(si.type, cd[index]));
				
				if (d instanceof DistribImpl)
					docs.add(new DocSendListner("Visit", ((DistribImpl)d).getRefVisit()));
				
				if (d instanceof DMPImpl)
					docs.add(new DocSendListner("Visit", ((DMPImpl)d).getRefVisit()));
			}
			
			index++;
		}
		
		return docs;
	}
}
