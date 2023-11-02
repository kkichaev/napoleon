package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

import java.util.ArrayList;

public class InvEquDoc extends DocType {
	public static final String OBJ_NAME = "InvEqu";
	
	private static InvEquDoc instance;
	
	protected InvEquDoc() {
		super(OBJ_NAME, OBJ_NAME, InvEquImpl.class);
	}
	
	public static InvEquDoc instance(){
		if (instance == null)
			instance = new InvEquDoc();
		
		return instance;
	}
	
	@Override public int getDocTitle() { return R.string.invequdoc_title;}
	
	@Override public int getResurceId() { return R.drawable.fridge; }

	@Override
	public int getResurce2Id() {
		return R.drawable.fridge_2;
	}

	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener res = super.getDirtyDocuments();

		ArrayList<Long> needRemove = new ArrayList<Long>();
		DocList docs = res.getDocuments();
		for(Document<?> d : docs) {
			CreatableDocument<?> doc = (CreatableDocument<?>) d;
			if( doc.isEmpty() ) {
				needRemove.add(doc.getRowid());
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();

		return res;
	}
}
