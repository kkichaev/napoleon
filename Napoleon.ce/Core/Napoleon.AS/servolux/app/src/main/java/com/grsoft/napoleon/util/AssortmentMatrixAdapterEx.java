package com.grsoft.napoleon.util;

import com.grsoft.napoleon.Warehouse;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.napoleon.WarehouseEx;

public class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapter {
	
	public AssortmentMatrixAdapterEx(Warehouse warehouse, String id) {
		super(warehouse, id);
	}
	
	@Override
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		if ( WarehouseEx.planItems.contains(id) && (WarehouseEx.orgMtxItems == null || WarehouseEx.orgMtxItems.contains(id)))
			super.addPriceInfo(rowid, folderid, name, id);
	}
	
}
