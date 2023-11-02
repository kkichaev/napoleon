package com.grsoft.util;

import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.WarehouseEx;
import com.grsoft.napoleon.WarehouseNewW;

public class MatrixItemsAdapterEx extends MatrixItemsAdapter {

	public MatrixItemsAdapterEx(WarehouseNewW warehouse, List<? extends MatrixItem> items) {
		super(warehouse, items);
	}

	@Override
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		if ( WarehouseEx.planItems.contains(id) && (WarehouseEx.orgMtxItems == null || WarehouseEx.orgMtxItems.contains(id)))
			super.addPriceInfo(rowid, folderid, name, id);
	}
}
