/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Order для работы с базой
 *
 * kki   25/10/2010   creating
 */
package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.OrderDeliveryDetail;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;

public class OrderImpl extends OrderImplBase<Order> implements Itemsable
{
	/**
	 * поле редактора свойств заявки если не заполнено - заявки не будут редактироваться
	 */
	public static PropertiesEditor 	OrderEditor;

	public interface PropertiesEditor {
		void edit(Context ctx, OrderImpl order, boolean isOldOrder);
	}
	
	@Override
	public void open(Context context) {
		if (data.number.length() == 0)
			OrderDetail.open(context, this);
		else
			OrderDeliveryDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context ) {
		PriceCount.open(context, itemRowid, (DbObject<Order>)this);
	}

	public void editProperties(Context ctx, boolean isOldOrder) {
		if( OrderEditor != null )
			OrderEditor.edit(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<Order> createInstance() { return new OrderImpl(); }
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		Order dest = copy.getData();
		if( (data.params & ParamState.ofCash) != 0)
			dest.params |= ParamState.ofCash;
		
		if(copy instanceof OrderImplBase && dest.items != null && dest.items.size() > 0){
			PriceImpl priceImpl = new PriceImpl();
			
			List<OrderItem> cp = new ArrayList<OrderItem>(dest.items);
			for(OrderItem item: cp){
				priceImpl.getData().id = item.id;
				int qty = item.qty;
				item.qty = 0;				
				if(qty !=0 && priceImpl.read())
					((OrderImplBase<?>)copy)
						.updateQty(priceImpl, qty, item.cost, item.inPack());
			}
			
			priceImpl.close();
		}
	}
}
