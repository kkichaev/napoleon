package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Agents;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	
	public OrderImplEx() {
		super();
		data.sumType = Agents.getPriceIndex();
	}
	
	@Override
	public String getDescription(Context context) {
		OrderEx oe = (OrderEx) data;
		if(oe.orderNumber.length() > 0)
			return oe.orderNumber;
		if(data.params == 0)
			return "Новый<br/>napoleon";
		if((data.params & ParamState.ofExported) != 0)
			return "Отправлен<br/>napoleon " + oe.orderTag;
//		if(oe.program.length() > 0 && !oe.program.equals("napoleon"))
//			return "<b>" + oe.program + "</b>";
		return super.getDescription(context);
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		((OrderEx)data).orderTag = Long.toHexString(data.created.getTime());
	}
	
	@Override
	public long write() {
		if(isEditable() && ((OrderEx)data).orderTag.length() == 0) {
			((OrderEx)data).orderTag = Long.toHexString(data.created.getTime());
		}
		return super.write();
	}
	
	@Override
	public long sum() {
		if(((OrderEx)data).fromKIS != 0 ) {
			long sum = 0;
			for(OrderItem oi : data.items) {
				sum += ((OrderItemEx)oi).sum;
			}
			
			return sum;
		}
		return super.sum();
	}
}
