package com.grsoft.dataobjects.impl;

import java.util.Calendar;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.CreateReturn;
import com.grsoft.napoleon.ReturnDetailEx;
import com.grsoft.napoleon.ReturnPriceCount;

public class ReturnImplEx extends ReturnImpl {
	public int editItemId = -1;
	
		@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		if (!isOldOrder){
			Calendar c = Calendar.getInstance();
			c.setTime(data.date);
			c.add(Calendar.DATE, 1);
			data.date = c.getTime();
			write();
			close();
		}
		
		CreateReturn.open(ctx, this, isOldOrder);
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this, editItemId);
	}

	public OrderItem findLast(Price p) {
		OrderItem ret = null;
		
		for(int i=data.items.size()-1; i>=0; i--) {
			OrderItem oi = data.items.get(i);
			if(oi.id.equals(p.id)) {
				ret = oi;
				break;
			}
		}
		
		return ret;
		
	}
	
	@Override
	public DataObject findUpdateItem(Price price) {
		if (editItemId >= 0 && editItemId < getData().items.size()) {
			OrderItem oi = getData().items.get(editItemId); 
			return (oi.id.equals(price.id)) ? oi : null;
		}
		return null;
	}

	@Override
	public void open(Context context) {
		ReturnDetailEx.open(context, this);
	}
}
