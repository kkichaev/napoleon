package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MainExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class DocumentsEx extends Documents {
	private ImageButton btnSync;
	private ImageButton btnSetting;
	
	static public void open(Context context) {
		Intent i = new Intent(context, DocumentsEx.class);
		
		i.putExtra(ExtrasConst.ORG_ID_STR, "");
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this));

		super.onCreate(savedInstanceState);
		btnSync = (ImageButton) findViewById(R.id.btnSync);
		btnSetting = (ImageButton) findViewById(R.id.btnSetting);
		
		btnSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 UpdateDB.open(v.getContext());
			}
		});
		
		btnSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Setting.open(v.getContext());
			}
		});
		
		unregisterForContextMenu(lvDocs);
	}
	
	@Override protected int getContentViewID() { return R.layout.documentsex; }

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == InvDoc.instance()) {
			DocType.setCurDoc(docType);
			DocList.open(this);
			finish();
			return;
		}
		super.adjustViewForDocType(docType);
	}
}
