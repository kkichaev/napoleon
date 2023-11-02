package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.R;

public class BonusDoc extends DocType {
	static BonusDoc instance;
	
	protected BonusDoc() {
		super("Бонус", "Bonus", BonusImpl.class);
	}
	
	public static BonusDoc instance() {
		if( instance == null )
			instance = new BonusDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.bonus; }
}
