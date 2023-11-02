package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	@Override
	protected void postOnCreate() {
//		cbPackets.setVisibility(View.INVISIBLE);
		super.postOnCreate();
		
		if(document instanceof OrderImpl)
			((OrderImpl) document).setUpdateQtyHandler(this);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		PriceEx pe = (PriceEx)price.getData();
		((OrderItemEx)item).isOurProduct = pe.isOurProduct;
	}
}
