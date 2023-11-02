package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvFrgImpl;
import com.grsoft.napoleon.R;

public class InvFrgDoc extends DateDocType {
	public static final String OBJ_NAME = "InvFrg";
	
	private static InvFrgDoc instance;
	
	protected InvFrgDoc() {
		super(OBJ_NAME, OBJ_NAME, InvFrgImpl.class);
	}
	
	public static InvFrgDoc instance(){
		if (instance == null)
			instance = new InvFrgDoc();
		
		return instance;
	}
	
	@Override public boolean outOfScript() { return true; }
	
	@Override public int getDocTitle() { return R.string.invfrgdoc_title;}
	
	@Override public int getResurceId() { return R.drawable.inv_frg; }
	@Override public int getResurce2Id() { return R.drawable.inv_frg_2; }

}
