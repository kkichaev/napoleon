package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
		
		findViewById(R.id.btnSendDocList).setVisibility(View.GONE);
	}
}
