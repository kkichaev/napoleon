package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.OrgInfoClickListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class DocumentsEx extends Documents {

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
