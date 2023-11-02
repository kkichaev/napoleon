package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;

public class DocumentsEx extends Documents {
	@Override
	protected void init(Bundle b) {
		super.init(b);
//		btnNewDoc.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				DocType dt = (DocType) DocType.getCurDoc();
//				OrgEx o = (OrgEx)org.getData();
//				if( dt == SalesDoc.instance() ) {
//					if(o.isStopList() ) {
//						Toast.makeText(DocumentsEx.this, R.string.org_blocked, Toast.LENGTH_SHORT).show();
//						return;
//					} else if (OrgHelper.cantLoad(o.id)) {
//						Toast.makeText(DocumentsEx.this, R.string.cant_load_org, Toast.LENGTH_SHORT).show();
//						return;
//					}
//				} 
//				doCreate();
//			}
//		});
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
}
