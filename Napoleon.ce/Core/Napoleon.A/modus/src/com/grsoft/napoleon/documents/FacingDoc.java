package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.FacingImpl;
import com.grsoft.napoleon.R;

public class FacingDoc extends DocType {
	public static final String OBJ_NAME = "Facing";
	public static FacingDoc instance = null;
	
	protected FacingDoc() {	super(OBJ_NAME, OBJ_NAME, FacingImpl.class);}
	
	static public DocType instance(){
		if(instance == null)
			instance = new FacingDoc();
		
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.facing;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.facing;
	}
}
