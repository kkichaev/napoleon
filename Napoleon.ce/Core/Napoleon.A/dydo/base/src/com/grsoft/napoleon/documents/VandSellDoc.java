package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.VandSellImpl;
import com.grsoft.napoleon.R;

public class VandSellDoc extends DocType {
	static DocType instance;
	
	public static DocType instance() {
		if( instance == null )
			instance = new VandSellDoc();
		return instance;
	}
	
	VandSellDoc() {
		super("Продажа", "VandSell", VandSellImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.order_doc;
	}
}
