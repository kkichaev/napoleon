package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.R;

public class BonusDoc extends OrderDoc {
	
	static private BonusDoc instance = null;
	
	BonusDoc() { super("Бонус", "Bonus", BonusImpl.class); }
	
	static public DocType instance() {
		if( instance == null )
			instance = new BonusDoc();
		
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.bonus_doc; }
	
	@Override
	public int getDocTytle() { return R.string.bonus; }
}
