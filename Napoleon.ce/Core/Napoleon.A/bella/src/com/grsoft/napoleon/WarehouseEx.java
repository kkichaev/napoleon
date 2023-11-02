package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	static int whIndex = 0;
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		int newIndex = 0;
		if( document instanceof OrderImplEx) {
			newIndex = ((OrderEx)document.getData()).whIndex;
		}
		if(whIndex != newIndex) {
			whIndex = newIndex;
			FoldersAdapter.resetCache();
		}
		
		return super.createListAdapter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
	
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
