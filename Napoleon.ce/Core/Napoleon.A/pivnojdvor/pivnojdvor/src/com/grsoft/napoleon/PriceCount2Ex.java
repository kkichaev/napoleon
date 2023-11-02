package com.grsoft.napoleon;

import com.grsoft.dataobject.OrderItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;

import android.widget.CheckBox;

public class PriceCount2Ex extends PriceCountEx  implements UpdateQtyHandler {
	CheckBox cbTag;
	
	@Override
	protected void refreshData() {
		// TODO Auto-generated method stub
		super.refreshData();
		
		if (document instanceof OrderImpl) {
			((OrderImpl) document).setUpdateQtyHandler(this);
			OrderItemEx item = (OrderItemEx) ((OrderImpl) document).findItem(price.getData().id);
			
			if (item != null)
				cbTag.setChecked(item.tag == 1);
			else
				cbTag.setChecked(false);
		}
	}
	
	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		
		cbTag = (CheckBox) findViewById(R.id.cbTag);
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).tag = cbTag.isChecked() ? 1 : 0; 
	}
}
