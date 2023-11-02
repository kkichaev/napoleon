package com.grsoft.napoleon;

import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;


public class NewstAdapter extends FoldersAdapter {

	public NewstAdapter(WarehouseManager warehouse) {
		super(warehouse);
	}

	@Override
	public String getWhereStr() {
		StringBuilder sb = new StringBuilder(super.getWhereStr());
		
		if(sb.length() > 0)
			sb.append(" AND ");
		
		sb.append("isNew=1");
		
		return sb.toString();
	}
}
