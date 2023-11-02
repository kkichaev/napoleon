package com.grsoft.napoleon;

import java.util.HashSet;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter fa = (FoldersAdapter)super.createListAdapter();
		fa.putFilter(new PriceFilter(document.getId()));
		return fa;
	}
}

class PriceFilter extends Filter {
	HashSet<String> items = null;
	
	public PriceFilter(String orgId) {
		super("PriceFilter" +  orgId);
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		if( oe.matrix.length() != 0) {
			OrgMatrixImpl mtx = new OrgMatrixImpl();
			OrgMatrix matrix = mtx.getData();
			matrix.name = oe.matrix;
			if( mtx.read() ) {
				items = new HashSet<String>();
				for(OrgMatrixItem omi: matrix.items)
					items.add(omi.id);
			}
			mtx.close();
		}
	}
	
	@Override
	public boolean inset(long priceRowID, String id) {
		if( items != null )
			return items.contains(id);
		return super.inset(priceRowID, id);
	}
}