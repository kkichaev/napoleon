package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SpancopDoc;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class DocumentsEx extends Documents {

	private List<DocTypeBase> filter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		filter = null;
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onlyVisitInit() {
		if (!ScriptDefImpl.canScripting()) {
			filter = ((NapoleonApp) getApplication()).potenzialOrgDocFilter;

			if (filter != null && filter.size() > 0
					&& !filter.contains(DocType.getCurDoc()))
				DocType.setCurDoc(filter.get(0));

			btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this, false, false, filter));
		}
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if(docType.equals(SpancopDoc.instance())){
			SpancopEdit.open(this, org.getData().id);
			finish();
		}else
			super.adjustViewForDocType(docType);
		
		if(docType.equals(OrderDoc.instance()) || docType.equals(ScriptDoc.instance()))
			btnNewDoc.setEnabled(!((OrgEx)org.getData()).isBlockedStop());
	}
	
	protected String getNonBlockingMessage(){
		return getString(R.string.client_in_stop_list);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.findItem(OptionsMenuHelper.MNU_NEW_DOC_ID);
		
		if(item != null && ((OrgEx)org.getData()).isBlockedStop())
			item.setVisible(false);
		
		return true;
	}
}
