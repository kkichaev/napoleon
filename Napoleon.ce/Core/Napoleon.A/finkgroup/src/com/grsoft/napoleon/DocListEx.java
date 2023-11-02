package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;

public class DocListEx extends DocList {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnSync).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { UpdateDB.open(v.getContext()); }
		});
		
		findViewById(R.id.btnSetting).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { Setting.open(v.getContext()); }
		});
	}
	
	@Override protected int getViewID() { return R.layout.doclistex; }
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == RemnantsDoc.instance()) {
			DocType.setCurDoc(docType);
			DocumentsEx.open(this);
			finish();
			return;
		}
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		int back = R.drawable.list_selector;
		if( doc instanceof GatherImpl && ((GatherImpl)doc).isComplete() )
			back = R.drawable.done_item;
		
		view.findViewById(R.id.tvSum).setVisibility(View.GONE);
		view.setBackgroundResource(back);
	}
}
