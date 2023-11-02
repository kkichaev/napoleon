package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.napoleon.VisitEditNew;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class VisitEditEx extends VisitEditNew implements OnClickListener{
	@Override
	protected CreatableDocument<? extends Visit> createDocument() {
		return (CreatableDocument<? extends DVisit>) DVisitDoc.instance().create(); 
	}

	static public void open(Context context, long rowid) {
		Intent i = new Intent(context, VisitEditEx.class);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);		
	}
	
	protected void refreshDocSum(Visit v) {
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnOK).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
	
	@Override
	protected boolean saveVisit() {
		((DVisitImpl)visit).setReadyToSend();
		return super.saveVisit();
	}
}
