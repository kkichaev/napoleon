package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderCancel;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	@Override
	public String getDescription(Context context) {
		return (data.number.length() > 0) ? 
				data.podRemark + "<br>" + data.number : 
				super.getDescription(context); 
	}
	
	@Override
	public int weight() {
		int wgh = 0;
		if( data.items != null ) {
			PriceImpl pi = new PriceImpl();
			PriceEx p = (PriceEx)pi.getData();
			for( OrderItem oi : data.items) {
				p.id = oi.id;
				if( pi.read() ) {
					wgh += (int)((long)p.volume * oi.qty / Consts.QTY_SCALE);
				}
			}
			pi.close();
		}
		
		return wgh / 100;
	}
	
	@Override
	public boolean delete() {
		if( !isProceeded() && isExported() ) {
			DbWriter w = new DbWriter();
			OrderCancel oc = new OrderCancel();
			DataObject.makeCopy(oc, data);
			oc.params = 0;
			w.insertRecord(oc);
			w.close();
		}
		return super.delete();
	}
	
	@Override
	public int getItemValue(Price item) {
		int qty = super.getItemValue(item);
		if (item.qtyInPack==0) item.qtyInPack = Consts.QTY_SCALE;
		qty = (int)((long)qty * Consts.QTY_SCALE / item.qtyInPack);
		return qty;
	}
	
	@Override
	public int getItemQty(Price item) {
		int qty = super.getItemQty(item);
		qty = (int)((long)qty * Consts.QTY_SCALE / item.qtyInPack);
		return qty;
	}
}
