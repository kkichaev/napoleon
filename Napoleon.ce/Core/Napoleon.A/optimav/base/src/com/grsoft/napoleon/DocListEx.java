package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;

public class DocListEx extends DocList {
	@Override
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeListener(){
			protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
				return !cd.isProceeded() && cd.isExported();
			}
		};
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(
				((AdapterView.AdapterContextMenuInfo)menuInfo).position);
		
		if(doc instanceof CreatableDocument<?> && (((CreatableDocument<?>)doc).isExported()))
			menu.findItem(R.id.itDelete).setVisible(false);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		lvDocs.setDividerHeight(1);
	}
	
	protected String getDocText(Org o, Document<?> doc) { return o.name; }
}
