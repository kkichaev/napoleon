package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;

public class WarehouseEx extends Warehouse {
	@Override
	protected void applayMatrix(String matrixName) {
		adapter.setExpanded(true);
		super.applayMatrix(matrixName);
	}
	
	@Override
	protected void resetMatrix() {
		adapter.setExpanded(false);
		super.resetMatrix();
	}

	@Override
	protected void createDocument() {
		super.createDocument();
		
	}
}
