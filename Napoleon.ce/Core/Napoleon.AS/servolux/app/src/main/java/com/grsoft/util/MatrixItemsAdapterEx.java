package com.grsoft.util;

import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.WarehouseEx;

public class MatrixItemsAdapterEx extends MatrixItemsAdapter {

	public MatrixItemsAdapterEx(Warehouse warehouse, List<? extends MatrixItem> items) {
		super(warehouse, items);
	}

	@Override
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		if ( WarehouseEx.planItems.contains(id) && (WarehouseEx.orgMtxItems == null || WarehouseEx.orgMtxItems.contains(id)))
			super.addPriceInfo(rowid, folderid, name, id);
	}
}
