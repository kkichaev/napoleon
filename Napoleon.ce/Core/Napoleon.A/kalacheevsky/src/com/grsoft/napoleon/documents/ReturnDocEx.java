package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ReturnImpl;

public class ReturnDocEx extends ReturnDoc {
	protected ReturnDocEx() {
		super(ReturnImpl.class);
	}
	
	public static void init() {
		instance = new ReturnDocEx();
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceTableName(Return.class, "Return");
		doi.replaceListType(Return.class, "items", ReturnItemEx.class);
	}
	
	@Override
	public boolean isCreatable() {
		return false;
	}
}
