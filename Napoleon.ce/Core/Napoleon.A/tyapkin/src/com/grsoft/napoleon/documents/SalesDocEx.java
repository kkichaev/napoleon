package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.network.DocExportListener;

public class SalesDocEx extends SalesDoc {
	
	public static void init() {
		instance = new SalesDocEx();
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener docs = super.getDirtyDocuments();

		boolean changed = false;
		DocList dl = docs.getDocuments();
		for( int i=0; i<dl.getCount(); i++ ) {
			SalesImpl sales = (SalesImpl) dl.get(i);
			if( sales.getData().items.size() == 0 ) {
				sales.delete();
				changed = true;
			}
		}
		dl.close();
		
		if( changed )
			docs = super.getDirtyDocuments();
		
		return docs;
	}
}
