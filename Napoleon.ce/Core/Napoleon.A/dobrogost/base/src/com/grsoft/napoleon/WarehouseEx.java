package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends WarehouseNew {
	
	protected FoldersAdapter createAdapterInstance() {
		return new FoldersAdapterEx(this);
	}
}
