package com.grsoft.ads.dataobjects.impl;

import java.util.List;

import android.content.Context;

import com.grsoft.ads.OrderTabActivity;
import com.grsoft.ads.database.OrderItem;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;

public class OrderImpl extends OrderItemsDocument<Order>
{
	public OrderImpl(){
		oldDocField = "planbegin";
	}

	@Override
	public void open(Context context) {
		OrderTabActivity.open(context, getRowid());
	}

	public boolean isDoing(){
		return (getData().params & Order.DOING_PARAMS) == Order.DOING_PARAMS;
	}
	
	public void setDoing(){
		getData().params = Order.DOING_PARAMS;
	}
	
	public boolean isDone(){
		return (getData().params & Order.DONE_PARAMS) == Order.DONE_PARAMS;
	}
	
	public void setDone(){
		getData().params = Order.DONE_PARAMS;
	}

	@Override
	public List<OrderItem> getOrderItems() {
		return getData().items;
	}
	
	@Override
	public boolean isEditable() {
		return !isDone() && !isRejected();
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(OrderItem oi : data.items) {
				if( oi.priceid.compareTo(itemId) == 0 )
					return oi;
			}
		
		return null;
	}

	@Override
	public int getItemColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemValue(Price itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemQty(String itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemSum(String itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost,
			boolean inPack) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setRejected(){
		data.params = Order.REJECTED | Order.DONE_PARAMS;
	}
	
	public boolean isRejected(){
		return ((data.params & Order.REJECTED) == Order.REJECTED);
	}
}
