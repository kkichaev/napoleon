package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.OrderDeliveryDetailEx;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public String getDescription(Context context) {
		String res = (data.podRemark.length() > 0) ? data.podRemark : 
			(isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			""; 
		if( data.number.length() > 0 )
			return (res.length() > 0 ) ? data.number + "<br><i>" + res + "</i>": data.number ;
		return res; 
	}
	
	@Override
	public void updateItemsCost(int sumType) {
		OrderEx order = (OrderEx) getData();
		order.sumType = sumType;
		PriceImpl priceImpl = new PriceImpl();
		Price p = priceImpl.getData();
		
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) this.getClass());
		try{
			for(OrderItem item : order.items){
				p.id = item.id;
				if (priceImpl.read()) {
					int newCost = cs.getItemCost(p, this);
					if( newCost != 0 ) {
						item.cost = newCost;
						((OrderItemEx)item).taxType = order.taxType;
					}
				}
			}
			
			if (write() != Consts.INVALID_ID)
				getDocumentType().refreshDocSum(order.id);
		}catch(Exception e){ 
			e.printStackTrace();
		}finally{
			priceImpl.close();
		}
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		Price prc = p.getData();
		int newQty = qty;
		
		if( ((CfgNpl)ConfigManager.getConfig()).checkPrice ) {
			int priceQty = prc.qty;
			if( item != null ) priceQty += item.qty;
			
			if( priceQty < qty ) {
				if( prc.qty < 0 ) newQty = 0;
				else {
					int qip = (prc.qtyInPack > 0) ? prc.qtyInPack : Consts.QTY_SCALE;
					// отрезаем дробную часть
					newQty = priceQty/ qip * qip;					
				}
			}
		}
		
		return newQty;
	}
	
	DeliveryItem getDlvItem(Delivery d, String id) {
		List<DeliveryItem> items = d.items;
		if( items != null ) {
			for( DeliveryItem i : items ) {
				if( i.id.equals(id) )
					return i;
			}
		}		
		return null;
	}

	@Override
	public long sum() {
		//
		// сумма считается с учетом измененных позиций
		//
		if( Features.DELIVERY_REPLACE_ORDER_SUM && data.number.length() > 0 && OrderDeliveryDetailEx.checker.haveData() ) {
			DeliveryImpl di = new DeliveryImpl();
			Delivery d = di.getData();
			d.id = data.id;
			d.number = data.number;
			boolean readed = di.read();
			di.close();
			if( readed ) {
				int sum = 0;
				for(OrderItem i : data.items) {
					if( OrderDeliveryDetailEx.checker.isChanged((OrderItemEx)i) ) {
						sum += (int)((long)i.cost * i.qty / Consts.QTY_SCALE);
					} else {
						DeliveryItem dlvitem = getDlvItem(d, i.id);
						sum += (dlvitem == null) ? 0 : dlvitem.sum;
					}
				}
				return sum;
			}
		}
		return super.sum();
	}
}
