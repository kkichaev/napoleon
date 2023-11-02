package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;

public class ReturnDetailEx extends ReturnDetail {
	@Override
	public void send() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();

		docs.add(new DocSendListner(ReturnDoc.instance().getObjectName(), doc));
		VisitImplEx vi = new VisitImplEx();
		if(vi.openAssociatedVisit((Return) doc.getData())) {
			docs.add(new DocSendListner(VisitDoc.instance().getObjectName(), vi));
		}
		vi.close();
		
		new DocumentSender(ReturnDetailEx.this, btnSend, docs, this).execute((Void[])null);
	}
}
