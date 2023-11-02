package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;

public class IncassImplEx extends IncassImpl{
	@Override
	public void postInit() {
		super.postInit();
		
		OrgImpl org = new OrgImpl();
		org.read("id", data.id);
		((IncassEx)data).supplyer = ((OrgEx)org.getData()).firm;
	}
}
