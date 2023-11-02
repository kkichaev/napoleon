package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class DocumentsEx extends Documents {
	ImageButton btnIncass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnIncass = (ImageButton) findViewById(R.id.btnIncass);
		btnIncass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { selectedType(IncassDoc.instance()); }
		});
	}
	
	@Override
	protected int getContentViewID() { return R.layout.documentsex;	}
	
	@Override
	protected void docDelete(CreatableDocument<?> doc) {
		super.docDelete(doc);
		
		if(DocType.getCurDoc() == IncassDoc.instance())
			DebtDocEx.instance().refreshDocSum(doc.getId());
	}
}
