package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.napoleon.R;

public class RejectActDoc extends DateDocType {
	static RejectActDoc instance;
	
	public static RejectActDoc instance() {
		if(instance == null) 
			instance = new RejectActDoc();
		return instance;
	}

	@Override public int getResurceId() { return R.drawable.reject_doc; }
	@Override public int getResurce2Id() { return R.drawable.reject_doc_2; }


	RejectActDoc() {
		super("Актирование", "RejectAct", RejectActImpl.class);
	}
}
