package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	HashMap<String, Integer> sales;
	
	@Override
	protected BaseAdapter createListAdapter() {
		String id = document.getId();
		sales = new HashMap<String, Integer>();
		if( id != null && id.length() > 0 ) {
			com.grsoft.napoleon.documents.DocList dl = OrderDoc.instance().docList(id);
			for(Document<?> doc : dl) {
				OrderImpl oi = (OrderImpl)doc;
				for(OrderItem item : oi.getData().items) {
					int qty = item.qty;
					Integer sq = sales.get(item.id);
					if( sq != null )
						qty += sq;
					sales.put(item.id, qty);
				}
			}
			dl.close();
		}
		return super.createListAdapter();
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView);
		TextView tv = (TextView)ret.findViewById(R.id.tvSales);
		Price p = price.getData();
		Integer qty = sales.get(p.id);
		tv.setText( (qty == null) ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE) );
		
		return ret;
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
}
