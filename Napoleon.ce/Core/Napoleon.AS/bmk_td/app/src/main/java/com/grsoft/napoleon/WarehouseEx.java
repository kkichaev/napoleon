package com.grsoft.napoleon;

import java.util.HashSet;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import android.view.Menu;
import android.widget.BaseAdapter;

public class WarehouseEx extends Warehouse {
	PriceImpl price = new PriceImpl();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		return true;
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		else
			return super.createListAdapter();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		price.close();
	}
	
//	@Override
//	protected Filter createZeroPositionFilter() {
//		return new ZeroFilter();
//	}
	
//	class ZeroFilter extends ZeroPositionFilter {
//
//		@Override public String getWhereStr() { return ""; }
//
//		@Override
//		public boolean inset(long priceRowID, String id) {
//			if( !(document instanceof Itemsable) )
//				return super.inset(priceRowID, id);
//
//			boolean result = false;
//
//			if(price.read(priceRowID))
//				result = (((Itemsable)document).getItemValue(price.getData()) > 0);
//			return result;
//		}
//	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
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
