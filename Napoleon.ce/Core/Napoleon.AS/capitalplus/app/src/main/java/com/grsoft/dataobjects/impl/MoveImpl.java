package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Move;
import com.grsoft.dataobjects.MoveItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.MoveDetail;
import com.grsoft.napoleon.MoveProperties;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MoveDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.FPOperation;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.content.Context;

public class MoveImpl extends CreatableDocument<Move> 
implements Itemsable{
	
	@Override
	public void open(Context context) {
		MoveDetail.open(context, this);
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (isExported())
					return;
				
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				
				if (updateQty(priceImpl, value, 0, false) &&
						context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
				priceImpl.close();
				
				MoveDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public long getValue() {
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				priceImpl.close();
				MoveItem mi = (MoveItem)findItem(priceImpl.data.id);
				
				return mi == null ? 0 : mi.qty;
			}
		});
	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(MoveItem mi : data.items) {
				if( mi.id.compareTo(itemId) == 0 )
					return mi;
			}
		
		return null;
	}

	@Override
	public int getItemColor() {return R.color.magneta;}

	int whIndex = -1; 
	
	public static int getWhIndex(String id) {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			List<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values,id);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	@Override public int getItemValue(Price item) {
		if(whIndex < 0)
			whIndex = getWhIndex(data.src);
		
		int result = item.qty;
		
		if (whIndex > 0  && whIndex <= ((PriceEx)item).whQty.size())  
			result = ((PriceEx)item).whQty.get(whIndex - 1).qty;
		return result;
	}

	@Override
	public int getItemQty(Price item) {
		MoveItem mi = (MoveItem) findItem(item.id);		
		return mi == null ? 0 : mi.qty;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost,
			boolean inPack) {
		Price price = priceImpl.getData();
		MoveItem item = (MoveItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				item = new MoveItem();
				item.id = price.id;
				item.qty = qty;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else
		{
			if( qty == 0 )
				data.items.remove(item);
			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		
		  if(super.init(context, orgId, gpsCoord))
			  MoveProperties.open(context, rowid, false);
		  
		  return false;
	}
	
	@Override
	public int getSumType() { return data.sumType; }

	public int weight() {
		int weight = 0;
				
		PriceImpl p = new PriceImpl();
		p.setReadingFields("weight");
		
		Price pd = p.getData();
		for (MoveItem item: data.items) {
			pd.id = item.id;
			
			if( p.read() )
				weight += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
		}
		
		p.close();
		
		return weight;
	}

	public int count() {
		int qty = 0;
		    	
    	if( data.items != null )
	    	for(MoveItem item : data.items )
	    		qty += item.qty;
    	
    	return qty / Consts.QTY_SCALE;
	}
	
	@Override
	public long sum() {
		PriceImpl priceImpl = new PriceImpl();
		
		long result = 0;
		if( data.items != null ) {
			for (MoveItem item: data.items){
				priceImpl.read("id", item.id);
				
				int cost = (int) CostStrategy.getInstance(
						(Class<? extends Document<?>>) getClass())
						.getItemCost(priceImpl.getData(), (Document<?>) this);
				
				result += FPOperation.itemMul(cost, item.qty, Consts.QTY_SCALE);
			}
		}
		
		return result;
	}
}
