package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;

import android.os.Bundle;
import android.view.View;

public class OrgTaskListEx extends OrgTaskList implements SendResultListener {
	@Override protected int getLayoutId() { return R.layout.orgtasklist_ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new DocumentSender(OrgTaskListEx.this, arg0, DocType.getCurDoc().getObjectName(),
						doc, doc.getRowid(), OrgTaskListEx.this).execute((Void[])null);
			}
		});
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result ) {
			doc.read(doc.getRowid(), false);
			
			if(!doc.isEditable()) {
				list.setOnItemClickListener(null);
				list.setOnItemLongClickListener(null);
			}
		}
	}
}
