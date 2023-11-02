package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.ImageButton;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.WorkTimeListener;

public class DocumentsEx extends Documents {

	WorkTimeListener wtl;
	
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
}
