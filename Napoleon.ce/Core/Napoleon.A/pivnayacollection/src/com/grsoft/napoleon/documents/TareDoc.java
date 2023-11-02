package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TareImpl;
import com.grsoft.napoleon.R;

public class TareDoc extends DateDocType {
	static TareDoc instance;
	
	public static TareDoc instance() {
		if(instance == null)
			instance = new TareDoc();
		return instance;
	}
	
	TareDoc() {
		super("Тара", "TareDoc", TareImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.tare; }
}
