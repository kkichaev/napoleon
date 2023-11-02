package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.napoleon.R;

public class DispatchReturnsDoc extends DocType {
	static DispatchReturnsDoc instance = null;
	public static DispatchReturnsDoc instance() {
		if(instance == null)
			instance = new DispatchReturnsDoc();
		return instance;
	}
	
	DispatchReturnsDoc() {
		super("Возврат из доставки","DispatchReturnsInfo", DispatchReturnsInfoImpl.class);
	}
	
	@Override public boolean isCreatable() { return false; }
	@Override public int getResurceId() { return R.drawable.return_notify; }
	@Override public int getResurce2Id() { return R.drawable.return_notify; }
}
