package com.grsoft.napoleon;

import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;


public class MfrAdapter extends FoldersAdapter {

	private String idMfr;


	public MfrAdapter(WarehouseManager warehouse, String idmfr) {
		super(warehouse);
		this.idMfr = idmfr;
	}

	@Override
	public String getWhereStr() {
		StringBuilder sb = new StringBuilder(super.getWhereStr());
		
		if(sb.length() > 0)
			sb.append(" AND ");
		
		sb.append("idMfr='").append(idMfr).append("'");	
		return sb.toString();
	}
}
