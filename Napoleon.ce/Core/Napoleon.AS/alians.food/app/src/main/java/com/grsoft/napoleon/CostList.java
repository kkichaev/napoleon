package com.grsoft.napoleon;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class CostList extends ArrayList<CostData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void loadCost(OrgEx org) {
		ConfigImpl cfg = new ConfigImpl();
		Config c = cfg.getData();
		
		c.key = "¬ид÷ены";
		if( cfg.read() ) {
			ArrayList<KeyValue> src = new ArrayList<KeyValue>();

			DialogHelper.makeListWithKey(c.value, src, "");
			CostData cd = findCost(src, org.costCode);
			if( cd != null )
				add(cd);
			
			if( org.costs != null )
				for( OrgCost oc : org.costs ) {
					KeyValue kv = (oc.cost < src.size()) ? src.get(oc.cost) : null;
					if( kv != null && !containsKey(kv.key.toString())) {
						add(new CostData(kv.value.toString(), kv.key.toString(), oc.cost));
					}
				}
		}
		cfg.close();
	}

	private boolean containsKey(String code) {
		for(CostData cd : this)
			if( cd.id.equals(code) )
				return true;
		return false;
	}

	CostData findCost(List<KeyValue> costs, String code) {
		CostData ret = null;
		int index = 0;
		for( KeyValue v : costs) {
			if( v.key.equals(code) )
				return new CostData(v.value.toString(), v.key.toString(), index);
			
			index++;
		}
		return ret;
	}
		
	public static void changeItemCost(Order doc, String newType, int newIndex) {
		int index = doc.sumType;
		if(doc.sumType == newIndex)
			return;
		
		PriceImpl pi = new PriceImpl();
		Price p =  pi.getData();
		
		for(OrderItem oi : doc.items) {
			if(oi instanceof OrderItemEx) {
				OrderItemEx oie = (OrderItemEx)oi;
				if(oie.costIndex == index) {
					oie.costCode = newType;
					oie.costIndex = newIndex;
					
					p.id = oi.id;
					pi.read();
					oie.cost = (newIndex < p.cost.size()) ? p.cost.get(newIndex).cost : 0;
				}
			} else if(oi instanceof SalesItemEx) {
				SalesItemEx sie = (SalesItemEx)oi;
				if(sie.costIndex == index) {
					sie.costCode = newType;
					sie.costIndex = newIndex;

					p.id = oi.id;
					pi.read();
					sie.cost = (newIndex < p.cost.size()) ? p.cost.get(newIndex).cost : 0;
					sie.countTax((Sales)doc, p.tax1);
				}
			}
		}
		
		pi.close();
	}
}
