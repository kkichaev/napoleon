package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.util.Consts;


public class ReturnImplEx extends ReturnImpl {
	@Override
	protected boolean checkPriceQty() {
		return true;
	}
	
	@Override
	public void editItem(long itemRowid, Context context ) {
		PriceCount.open(context, itemRowid, this); 
	}

	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		return qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		((PricePrint)price.getData()).vanQty += -qty;
		price.write();
	}
	
	@Override
	public int getItemValue(Price item) {
//		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNpl)ConfigManager.getConfig()).isPackView )
//			return (int)((long)((PricePrint)item).vanQty * Consts.QTY_SCALE / item.qtyInPack);

		return ((PricePrint)item).vanQty;
	}

	public int countPack() {
    	int qty = 0;
    	
    	if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("qtyInPack");
			
			PricePrint pd = (PricePrint) p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() ) {
					int qip = (pd.qtyInPack == 0) ? Consts.QTY_SCALE : pd.qtyInPack;
					qty += (int)((long)item.qty * Consts.QTY_SCALE / qip);
				}
			}
			p.close();
    	}
    	
    	return qty / Consts.QTY_SCALE;
	}
}
