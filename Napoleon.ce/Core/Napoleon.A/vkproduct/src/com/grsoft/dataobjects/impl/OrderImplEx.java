package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl {
	int whIndex = -1; 

	@Override
	public int weight() {
		int weight = 0;
		
		if( !Features.NO_WEIGHT_IN_ORDER && data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("weight");
			
			Price pd = p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( ((OrderItemEx)item).inKG > 0  )
					weight += item.qty;
				else  if( p.read() )
					weight += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return weight;
	}
	
	int getWhIndex() {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values, ((OrderEx)data).whCode);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		int index = ((OrderEx)data).whIndex;
		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		return ( index == 0 || index > whQty.size() ) ?  item.qty : whQty.get(index-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	public void resetSklad() {
		whIndex = -1;
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> o) {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.getData().date = c.getTime();
	}
}
