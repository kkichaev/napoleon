package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.napoleon.R;

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
	
	@Override public boolean outOfScript() { return true; }
	
	@Override public int getDocTitle() { return R.string.invequdoc_title;}
	
	@Override public int getResurceId() { return R.drawable.fridge; }

	@Override
	public int getResurce2Id() {
		return R.drawable.fridge_2;
	}
}
