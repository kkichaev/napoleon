package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected void postAdapterInit() {
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		
		OrgMatrixImpl m = new OrgMatrixImpl();
		m.read("id", ((OrgEx)org.getData()).idType);
		
		String mtx = m.getData().mtx;
		
		if(mtx.trim().length() == 0)
			adapter.buildSet();
		else
			applayMatrix(mtx);
	}
}
