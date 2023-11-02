package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof ReturnImplEx) {
			FoldersAdapter.resetCache();
			return new ReturnAdapter(this, document.getId());
		}
		return super.createListAdapter();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
	
	public static void resetCache() { 
		idStore = "";
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);			
			return result;
		}
	}
}
