package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void init() {
		btnSend.setEnabled(((OrderEx)doc.getData()).fromKIS == 0);
	}

	@Override
	public void send() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(docType.getObjectName(), doc, doc.getRowid()));
		
		long rid = RemnantsImpl.find(doc.getId(), doc.getData().created);
		if( rid != ExtrasConst.INVALID_ROWID) {
			RemnantsImpl ri = new RemnantsImpl();
			ri.read(rid);
			if(ri.isExported() == false)
				docs.add(new DocSendListner(RemnantsDoc.instance().getObjectName(), ri, ri.getRowid()));
			ri.close();
		}
		
		new DocumentSender(OrderDetailEx.this, btnSend, docs).execute((Void[])null);
	}
}
