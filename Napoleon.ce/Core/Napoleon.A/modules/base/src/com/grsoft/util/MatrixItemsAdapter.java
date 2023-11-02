package com.grsoft.util;

import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.WarehouseNewW;

public class MatrixItemsAdapter extends MatrixBaseAdapter {
	private List<? extends MatrixItem> items;
	String name;
	
	public MatrixItemsAdapter(WarehouseNewW warehouse, List<? extends MatrixItem> items) {
		this(warehouse, items, null);
	}
	
	public MatrixItemsAdapter(WarehouseNewW warehouse, List<? extends MatrixItem> items, String name) {
		super(warehouse);
		this.items = items;
		this.name = name;
	}

	public String getName() { return (name == null) ? "MatrixItemsAdapter" : name; }

	@Override
	protected List<? extends MatrixItem> getMatrixItems() { return items;	}

}
