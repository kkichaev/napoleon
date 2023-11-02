package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.util.DatePeriod;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class IncassDocList extends DocList {
	DocType prevDocType;
	int noncash = 0;
	
	static void open(Context context) {
		Intent i = new Intent(context, IncassDocList.class);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prevDocType = DocType.getCurDoc();
		super.onCreate(savedInstanceState);
		btnDocFilter.setVisibility(View.GONE);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(IncassDoc.instance());
	}
	
	protected int getFilterLayout() { return R.layout.date_selection_incass; }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			DocType.setCurDoc(prevDocType);
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapter(this, docType, saveDatePeriod){
			@Override
			public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
				com.grsoft.napoleon.documents.DocList result = super.fillDocList(docType, orgId, order, dp);
				
				List<Long> toRemoveIds = new ArrayList<Long>();
				for (Document<?> curDoc : result) {
					if (curDoc instanceof IncassImpl ) {
						IncassEx i = (IncassEx) curDoc.getData();
						
						if(i.noncash != noncash)
							toRemoveIds.add(curDoc.getRowid());
					}
				}
				
				result.removeDocuments(toRemoveIds);
				
				return result;
			}
		};
	}
	
	@Override
	protected void filterClick(DialogInterface dialog) {
		noncash = ((RadioButton)((Dialog)dialog).findViewById(R.id.rbCash)).isChecked() ? 0 : 1;
		super.filterClick(dialog);
	}
}
