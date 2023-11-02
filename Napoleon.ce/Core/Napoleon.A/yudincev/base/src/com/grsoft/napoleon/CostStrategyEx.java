package com.grsoft.napoleon;

import com.grsoft.dataobjects.CostItemEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		
		if(doc instanceof OrderImplEx) {
			int sumType = doc.getSumType();
			int sale = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? ((CostItemEx)p.cost.get(sumType)).sale : 0;			
			
			if( sale == 0 ) {
				result -= (int) (((long) result * ((OrderEx)doc.getData()).discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
			}
		}
		return result;
	}
}
