package com.grsoft.napoleon;

import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.DiscountPriceItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int dsc = 0;
		if( doc instanceof OrderImpl ) {
			OrderImpl oi = (OrderImpl)doc;
			OrderEx oe = (OrderEx)oi.getData();
			OrderItem item = null;
			if( oi != null ) {
				if( oi.getRowid() == ExtrasConst.INVALID_ID) {
					int sumType = doc != null ? doc.getSumType() : 0;
					if( p.cost.size() > sumType )
						return p.cost.get(sumType).cost;
				}
				item = (OrderItem)oi.findItem(p.id);
			}
			dsc = getDiscount(p, item, oe.id, oe.dogovor);
		}
		int cost = super.getItemCost(p, doc);
		if( dsc != 0 )
			cost += (cost * dsc) / (Consts.SUM_SCALE * Consts.SUM_SCALE);
		return cost;
	}

	OrgImpl org = new OrgImpl();
	DiscountImpl di1 = new DiscountImpl();
	DiscountImpl di2 = new DiscountImpl();

	private int getDiscount(Price p, OrderItem item, String id, String dogId) {
		int dsc = 0;

		if( org.getData().id.equals(id) == false ) {
			org.getData().id = id;
			org.read();
		}
		
		String ido = ((OrgEx)org.getData()).ido;
		Discount d1 = di1.getData();
		Discount d2 = di2.getData();
		if( d1.id.equals(ido) == false) {
			d1.id = ido;
			d1.dogovor = "";
			di1.read();
		}
		
		if( d2.dogovor.equals(dogId) == false || d2.id.equals(ido) == false ) {
			d2.id = ido;
			d2.dogovor = dogId;
			di2.read();
		}
		
		int pindex = PriceHelper.get(p.id);
		
		DiscountImpl[] darray = { di1, di2 };
		for( DiscountImpl d : darray) {
			if( d.getRowid() == ExtrasConst.INVALID_ID )
				continue;
			for( DiscountItem ditem : d.getData().items ) {
				if( canDiscount(pindex, ditem, item) == false )
					continue;
				
				if( dsc == 0 )
					dsc = -ditem.discount;
				else {
					// для скидок i->discount > 0 выбираем максимальную, для наценок i->discount < 0 - выбираем наименьшую
					if( ditem.discount > -dsc )
						dsc = -ditem.discount;
				}
			}
		}
		return dsc;
	}

	private boolean canDiscount(int pindex, DiscountItem ditem, OrderItem item) {
		boolean found = false;
		for( DiscountPriceItem i : ditem.items ) {
			if( i.index == pindex ) {
				found = true;
				break;
			}
		}
		if( !found || (item != null && ditem.qty > item.qty) )
			return false;
		
		return true;
	}
}
