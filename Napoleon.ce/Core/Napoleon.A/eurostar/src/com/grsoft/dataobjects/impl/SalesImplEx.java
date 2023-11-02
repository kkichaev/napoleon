package com.grsoft.dataobjects.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy.ISupplyer;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class SalesImplEx extends SalesImpl implements ISklad, ISupplyer{
int whIndex = -1; 
	
	public static int getWhIndex(String whCode) {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values, whCode);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	public int getWhIndex() {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((SalesEx)data).whCode);
		return whIndex;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((SalesEx)data).whCode);

		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		updateQtyPrice(price, qty);
	}
	
	@Override
	protected void updateQtyPrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((SalesEx)data).whCode);

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			price.updateQty(qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
		
		getDocumentType().refreshDocSum(data.id);
		DebtDoc.instance().refreshDocSum(data.id);
	}
	
	@Override
	protected void processInit(OrderImplBase<?> src) {
		SalesEx dest = (SalesEx)data;
		OrderEx oex = (OrderEx) src.getData();
		for(Field sf : oex.getClass().getDeclaredFields()) {
			if( (sf.getModifiers() & (Modifier.PUBLIC|Modifier.STATIC)) == Modifier.PUBLIC ) {
				try {
					Field df = dest.getClass().getDeclaredField(sf.getName());
					df.set(dest, sf.get(oex));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getSupplyer() { return data.supplyercode;	}
}
