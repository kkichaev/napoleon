package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ReturnChekBackImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class ReturnChekBackDoc extends DocType {
	static ReturnChekBackDoc instance = null;
	
	public static ReturnChekBackDoc instance() {
		if(instance == null)
			instance = new ReturnChekBackDoc();
		return instance;
	}
	
	public ReturnChekBackDoc() {
		super("Отмена чека", "ReturnChekBack", ReturnChekBackImpl.class);
	}

	@Override
	public int getDocTitle() { return R.string.cancel_chek_doc_title; }

	@Override
	public int getResurceId() {
		return R.drawable.money_back;
	}

	@Override
	public int getResurce2Id() { return R.drawable.money_back_2; }

	@Override public DocExportListener getDirtyDocuments() { return null; }
}
