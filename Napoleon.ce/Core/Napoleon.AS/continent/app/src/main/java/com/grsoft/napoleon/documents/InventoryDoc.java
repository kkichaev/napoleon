package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InventoryImpl;

public class InventoryDoc extends DocType {
	static InventoryDoc instance;

	public static InventoryDoc instance() {
		if (instance == null)
			instance = new InventoryDoc();
		return instance;
	}

	protected InventoryDoc() {
		super("Inventory", "Inventory",  InventoryImpl.class);
	}

}
