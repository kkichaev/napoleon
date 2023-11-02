package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
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
		filter = ((NapoleonApp)getApplication()).potenzialOrgDocFilter;
		
		if (filter != null && filter.size() > 0 && 
				!filter.contains(DocType.getCurDoc()))
			DocType.setCurDoc(filter.get(0));
		
		btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this, false, 
				filter));
	}
}
