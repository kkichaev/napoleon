package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PhoneCallImpl;
import com.grsoft.napoleon.R;

public class PhoneCallDoc extends DateDocType {
	static PhoneCallDoc instance = null;
	
	public static PhoneCallDoc instance() {
		if(instance == null)
			instance = new PhoneCallDoc();
		return instance;
	}
	
	PhoneCallDoc() {
		super("Звонок","PhoneCall",PhoneCallImpl.class);
	}
	
	@Override public int getDocTitle() { return R.string.phone_call; }
	@Override public int getResurceId() { return R.drawable.phone_doc; }
	@Override public int getResurce2Id() { return R.drawable.phone_doc; }
}
