package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.impl.ArchIncassImpl;
import com.grsoft.napoleon.documents.ArchIncassDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.ExtrasConst;

public class ArchIncassEdit extends IncassEditEx {
	public static void open(Context context, ArchIncassImpl doc) {
		Intent i = new Intent(context, ArchIncassEdit.class);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		doc = new ArchIncassImpl();
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnPrint).setVisibility(View.GONE);
	}
	
	@Override
	protected boolean save() {
		return false;
	}
	
	@Override
	protected void send() {
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				ArchIncassDoc.instance().getObjectName(), doc, doc.getRowid());
			ds.execute((Void[])null);
	}
}
