package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class ItemActionData {
	public List<ServikoAction> base = new ArrayList<ServikoAction>();
	public List<ServikoAction> actions = new ArrayList<ServikoAction>();

	public ActionResult count(int price, String actionId) {
//		int baseCost = price;
//		for(ServikoAction sab : base) {
//			if(sab.isFix > 0) {
//				baseCost = sab.value / 10;
//			} else {
//				baseCost -= sab.discountValue(price, baseCost);
//			}
//		}
//		
//		int finalCost = baseCost;
//		if(actionId != null) {
//			for(ServikoAction sab : actions) {
//				if(!sab.id.equals(actionId))
//					continue;
//				
//				if(sab.isFix > 0) {
//					finalCost = sab.value / 10;
//				} else {
//					finalCost -= sab.discountValue(price, baseCost);
//				}
//			}
//		}
		
		ActionResult res = new ActionResult();
		res.cost = price;
		if(actionId != null) {
			for(ServikoAction sab : actions) {
				if(!sab.id.equals(actionId) && !sab.isBaseAction())
					continue;
				
				if(sab.isFix > 0) {
					if(sab.isBaseAction()) {
						res.cost = sab.value / 10;
						if(sab.condition.length() > 0) {
							ActionResultItem ari = new ActionResultItem();
							ari.action = sab;
							ari.value = res.cost;
							res.conditions.add(ari);
						}
					} else {
						res.cost = sab.value / 10;
					}
				} else {
					if(sab.isBaseAction()) {
						int dv =  sab.discountValue(price, res.cost);
						res.cost -= dv;
						if(sab.condition.length() > 0) {
							ActionResultItem ari = new ActionResultItem();
							ari.action = sab;
							ari.value = dv;
							res.conditions.add(ari);
						}
					} else {
						res.cost -= sab.discountValue(price, res.cost);
					}
				}
			}
		}
		
		return res;
	}
}
