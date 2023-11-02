package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.Set;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof ReturnImplEx) {
			FoldersAdapter.resetCache();
			return new ReturnAdapter(this, document.getId());
		}
		return super.createListAdapter();
	}	
	
	class ReturnAdapter extends FoldersAdapter {

		Set<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
}
