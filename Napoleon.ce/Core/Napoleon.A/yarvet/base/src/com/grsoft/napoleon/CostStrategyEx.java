package com.grsoft.napoleon;

import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = 0;
		if( Features.COST_MANAGER != null && doc != null ) {
			int idx = Features.COST_MANAGER.getCostIndex(doc.getId());
			if(idx >= 0)
				result = Features.COST_MANAGER.getCost(p.id, idx);
		}
		if(result > 0)
			return result;
		
		int sumType = doc != null ? doc.getSumType() : 0;
//		if( Features.CAN_CHANGE_COST && doc != null && doc instanceof OrderImplBase<?>) {
//			OrderItem oi = (OrderItem)((OrderImplBase<?>)doc).findItem(p.id);
//			if( oi != null )
//				return oi.cost;
//		}
//		if( Features.COST_MANAGER != null ) {
//			result = Features.COST_MANAGER.getCost(p.id, sumType);
//			if(result == 0)
//				result = super.getItemCost(p, doc);
//		} else {
			result = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;		
					
			if (doc instanceof OrderImplBase<?>){
				int discount = ((IOrder)doc.getData()).getDisc();
				result -= (int)(((long)result * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / 
						(Consts.SUM_SCALE * Consts.SUM_SCALE));
			}		
//		}
		
		return result;
	}
}
