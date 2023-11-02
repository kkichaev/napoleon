package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menu_ex;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return null;
	}
	
	@Override protected void updateTotalSum() {}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		else if ( document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ROWID)
			return new OrderAdapter(this);
		
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
	
	static class OrderAdapter extends FoldersAdapter
	{
		public OrderAdapter(WarehouseManager warehouse) {
			super(warehouse);
			
			putFilter(new Filter("OrderFilter") {
				@Override
				public String getWhereStr() {
					return "ordflt = 1";
				}
			});
			
		}
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		readPriceNode(node.getRowid());
		Price p = price.getData();

		View view;
		int id = R.layout.priceitemrowex;
		
		if (convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		setName(view, p, 1, node);

		TextView tvCost = (TextView) view.findViewById(R.id.tvCost);
		setTextColumnValue(tvCost, COLUMN_COST, p);
				
		return view;
	}
}
