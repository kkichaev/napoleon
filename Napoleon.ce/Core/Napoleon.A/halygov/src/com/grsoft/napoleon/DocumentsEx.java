package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.ImageButton;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.WorkTimeListener;

public class DocumentsEx extends Documents {

	WorkTimeListener wtl;
	
	public static String makeOrgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String info = oe.name;
		info += "<br>Лимит: " + oe.limit;
		info += "<br>График оплат: " + oe.payData;
		return info;
	}
	
	@Override protected String orgInfo(Org o) { return makeOrgInfo(o); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wtl = new WorkTimeListener((NapoleonApp)getApplication(), org.getData().id, (ImageButton) findViewById(R.id.btnStart), btnNewDoc);
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
		
	@Override
	public void onBackPressed() {
		if( wtl.isInWork() )
			return;
		super.onBackPressed();
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return wtl.isInWork() && super.canCreateDoc(docType);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else {
			super.adjustViewForDocType(docType);
		}
	}
}
