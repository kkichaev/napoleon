package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class RequestCheckDoc extends DocType {
	static RequestCheckDoc instance = null;
	
	public static RequestCheckDoc instance() {
		if(instance == null)
			instance = new RequestCheckDoc();
		return instance;
	}
	
	public RequestCheckDoc() {
		super("Чек", "RequestChek", RequestChekImpl.class);
	}

	@Override
	public int getDocTitle() { return R.string.req_chek_doc_title; }

	@Override
	public int getResurceId() {
		return R.drawable.money_req;
	}

	@Override
	public int getResurce2Id() { return R.drawable.money_req_2; }

	@Override public DocExportListener getDirtyDocuments() { return null; }
}
