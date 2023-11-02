package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;

import android.content.Context;


public class DistribImpl extends CreatableDocument<Distrib> implements Itemsable {

	@Override
	public void open(Context context) {
		Warehouse.open(context, this, true);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		GoodsHelper.fillDocItems(data.id, data.items, 
				DataObjectInfo.getInstance().getListType(data.getClass(), "items"));
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		if (super.init(context, orgId, gpsCoord))
			Warehouse.open(context, this, true);
		return false;
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		if(!isEditable())
			return;
		
		PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		price.close();
		
		DistribItem item = (DistribItem) findItem(price.getData().id);
		
		if(item != null){
			item.exist = item.exist == 0 ? 1 : 0;
			write();
			close();
			
			if (context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
		}
	}

	@Override
	public DataObject findItem(String itemId) {
		DistribItem result = null;
		for(DistribItem i : data.items){
			if(i.id.equals(itemId)){
				result = i;
				break;
			}
		}
		return result;
	}

	@Override public int getItemColor() { return R.color.green;}

	@Override
	public int getItemValue(Price item) { return 0;	}

	@Override public int getItemQty(Price item) { return 0; }

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		boolean result = true;
		
		for(DistribItem i : data.items)
			if(i.exist > 0){
				result = false;
				break;
			}
		
		return result;
	}
}
