package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DiscountMonitoring;
import com.grsoft.dataobjects.DiscountMonitoringItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.MntrDetail;
import com.grsoft.napoleon.MntrItemEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.GpsCoord;

import android.content.Context;
import android.graphics.Color;

public class DiscountMonitoringImpl extends CreatableDocument<DiscountMonitoring> implements Itemsable {

	@Override public void open(Context context) { MntrDetail.open(context, this); }

	@Override
	public void editItem(long itemRowid, Context context) {
		MntrGoodsImpl mi = new MntrGoodsImpl();
		mi.read(itemRowid);
		MntrItemEdit.open(context, this, mi.getData().id);		
	}

	@Override
	public DataObject findItem(String itemId) {
		for(DiscountMonitoringItem i : data.items)
			if(i.id.equals(itemId))
				return i;
		return null;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		Warehouse.open(context, this, false);
		return false;
	}

	@Override public int getItemColor() { return R.color.item_highlight; }

	@Override
	public int getItemValue(Price item) {
		DiscountMonitoringItem i = (DiscountMonitoringItem) findItem(item.id);
		return i == null ? 0 : i.qty;
	}

	@Override
	public boolean isEmpty() {
		return data.items.size() == 0;
	}
	
	@Override public int getItemQty(Price item) { return getItemValue(item); }
	@Override public long getItemSum(Price item) { return 0; }
	@Override public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) { return false; }

	public void deleteItem(String id) {
		for(DiscountMonitoringItem i : data.items)
			if(i.id.equals(id)) {
				data.items.remove(i);
				write();
				return;
			}
	}
	
	public void updateItem(String id, int qty, int facing, int cost, boolean haveAction) {
		DiscountMonitoringItem i = (DiscountMonitoringItem) findItem(id);
		if(i == null) {
			i = new DiscountMonitoringItem();
			i.id = id;
			data.items.add(i);
		}
		
		i.qty = qty;
		i.cost = cost;
		i.facing = facing;
		i.isAction = haveAction ? 1 : 0;
		
		write();
	}
}
