package com.grsoft.napoleon;

import android.view.View;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;


public class DocListEx extends DocList {
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, docType, saveDatePeriod, R.layout.docs_list_row2_ex);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		if (DocType.getCurDoc() == OrderDoc.instance())
			OrderHelper.setDriverView(view, (OrderEx)doc.getData());
	}
}
