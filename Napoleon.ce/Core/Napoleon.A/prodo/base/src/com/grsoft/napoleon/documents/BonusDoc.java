package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.R;

public class BonusDoc extends OrderDoc {
	static public final String DOC_NAME = "Дегустация";
	static public final String OBJ_NAME = "Bonus";
	static protected BonusDoc instance = null;
	
	public BonusDoc(){
		super(DOC_NAME, OBJ_NAME, BonusImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new BonusDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.bonus_doc; }
	
	@Override
	public int getDocTitle() {	return R.string.bonus;	}
}
