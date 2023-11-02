package com.grsoft.napoleon.documents;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.R;

import android.content.Context;

public class ClientCardDoc extends DocType {
	static public final String OBJ_NAME = "ClientCard";
	private static ClientCardDoc instance = null;
	
	static public DocType instance() {
		if( instance == null )
			instance = new ClientCardDoc();
		
		return instance;
	}
	
	protected ClientCardDoc() {
		super(OBJ_NAME, DummyImpl.class);
	}

	@Override
	public int getDocTitle() { 
			return R.string.client_card_doc_title; 
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.clientcard;
	}
	
	@Override
	public boolean outOfScript() {
		return true;
	}
	
	@TableInfo(name="clientcard", keyFields="created")
	private static class DummyObject extends CreateDocDataObject {}
	
	private static class DummyImpl extends CreatableDocument<DummyObject> {
		@Override public void open(Context context) { }
		@Override public boolean read() { return true; }
		@Override public long write() {	return 0;}
		@Override public long insert() { return 0;}
	}
	
}
