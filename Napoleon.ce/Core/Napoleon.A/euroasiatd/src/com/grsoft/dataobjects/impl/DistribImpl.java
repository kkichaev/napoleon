package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgDistrib;
import com.grsoft.dataobjects.OrgDistribItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.DistribDetail;
import com.grsoft.napoleon.DistribEdit;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlgEx;
import com.grsoft.napoleon.InputNumberEx;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class DistribImpl extends CreatableDocument<OrgDistrib> implements Itemsable {

	@Override
	public void open(Context context) {
		DistribDetail.open(context, this);
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		if(super.init(context, orgId, gpsCoord))
			DistribEdit.open(context, getRowid(), false);
		
		return false;
	}
	
	@Override
	public void editItem(final long itemRowid, final Context context) { 
		InputNumberDlgEx.open(context, new InputNumberEx() {
			
		@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		
		@Override
		public void applayInput(int[] value, Object... params) {
			
			if (isExported())
				return;
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			
			boolean refresh = false;
			if( value[0] == 0 && value[1] == 0 && editValue.length() == 0) {
				refresh = deleteItem(priceImpl.getData());
			} else 
				if( Features.REST_IN_PACK )
					value[0] = (int)((long)value[0] * priceImpl.getData().qtyInPack / Consts.QTY_SCALE);
				refresh = updateQty(priceImpl, value[0], value[1], false);
			if (refresh && context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
			
			priceImpl.close();
			
			DistribDoc.instance().refreshDocSum(data.id);
		}

		@Override
		public int[] getValues() {
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			OrgDistribItem ri = (OrgDistribItem)findItem(priceImpl.data.id);
			int qty = ri == null ? 0 : ri.qty;
			
			if( Features.REST_IN_PACK )
				qty = (int)((long)qty * Consts.QTY_SCALE / priceImpl.getData().qtyInPack);
			
			int[] result = new int[2];
			
			if(ri != null){
				result[0] = qty;
				result[1] = ri.cost;
			}

			
			return result;
		}
	});}

	@Override
	public DataObject findItem(String itemId) {
		
		if( data.items != null )
			for(RemnantItem ri : data.items) {
				if( ri.id.compareTo(itemId) == 0 )
					return ri;
			}
		
		return null;
	}

	@Override
	public int getItemColor() { return R.color.distrib_item_color; }

	@Override
	public int getItemValue(Price item) { 
		return item.qty;
	}

	@Override
	public int getItemQty(Price item) {
		RemnantItem ri = (RemnantItem) findItem(item.id);		
		return ri == null ? 0 : ri.qty;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {	
		Price price = priceImpl.getData();
		OrgDistribItem item = (OrgDistribItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty >= 0 )
			{
				Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

				try {
					item = (OrgDistribItem) itemClass.newInstance();
					
					item.id = price.id;
					item.qty = qty;
					data.items.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				needUpdate = false;
		} else
		{
//			if( qty == 0 )
//				data.items.remove(item);
//			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
//			}
		}
		
		item.cost = cost;
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
	
	public boolean deleteItem(Price item) {
		boolean result = false;
		DataObject ditem = findItem(item.id);
		
		if(ditem != null){
			data.items.remove(ditem);
			write();
			result = true;
		}
		
		return result;
	}
	
	@Override
	public boolean isEmpty() {
		return data.items.size() == 0;
	}
}
