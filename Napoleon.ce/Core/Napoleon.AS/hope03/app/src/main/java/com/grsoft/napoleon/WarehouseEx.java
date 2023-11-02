package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.dataobjects.DistribMatrix;
import com.grsoft.dataobjects.DistribMatrixItem;
import com.grsoft.dataobjects.impl.DistribMatrixImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.widget.BaseAdapter;

public class WarehouseEx extends Warehouse {
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof RemnantsImpl)
			return new RemnantsAdapter(this, document.getId());

		return super.createListAdapter();
	}
	
	class RemnantsAdapter extends FoldersAdapter {

		HashSet<String> items = new HashSet<String>();
		
		public RemnantsAdapter(WarehouseManager warehouse, String id) {
			super(warehouse);
		
			DistribMatrixImpl di = new DistribMatrixImpl();
			DistribMatrix dm = di.getData();
			dm.id = id;
			di.read();
			di.close();
		
			for( DistribMatrixItem item : dm.items)
				items.add(item.id);
		}
		
		@Override
		public boolean inset(long rowid, String id, int folder) {
			if( !items.contains(id))
				return false;
			return super.inset(rowid, id, folder);
		}
	}
}
