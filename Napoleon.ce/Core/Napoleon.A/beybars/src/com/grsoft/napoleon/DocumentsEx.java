package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;

public class DocumentsEx extends Documents {
	
	Boolean haveUPDocs = null;
	
	@Override
	protected boolean isOrgBlocked(Org o, DocType dt) {
		boolean isBlocked = super.isOrgBlocked(o, dt); 
		return isBlocked || haveUnpayedDocs();
	}

	private boolean haveUnpayedDocs() {
		if( haveUPDocs == null ) {
			int count = 0;
			DocList dl = DeliveryDoc.instance().docList(org.getData().id);
			for(Document<?> doc : dl) {
				if(((Delivery)doc.getData()).sumD > 0) {
					count++;
					if( count >= 3 )
						break;
				}
			}
			dl.close();
			haveUPDocs = (count >= 3);
		}
		return haveUPDocs;
	}
	
	@Override
	protected String getNonBlockingMessage() {
		if( haveUPDocs != null && haveUPDocs ) {
			return getString(R.string.unpayed_docs_message) + " " +
					getString(R.string.order_cant_processing);
		}
		return super.getNonBlockingMessage();
	}
}
