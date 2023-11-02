package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyOrder extends CostStrategy{
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		
		if (result > 0 && doc instanceof OrderImpl){
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
			if( oi != null ) {
				String unitCode = oi.unit;				
				if (unitCode.length() > 0){
					int coeff = 0;
					
					for(UnitItem ue : ((PriceEx)p).units)
						if (ue.id.equals(unitCode)){
							coeff = ue.coef;
							break;
						}
					
					if (coeff > 0)
						result = result * coeff / Consts.SUM_SCALE;
				}
			}
		}
		
		return result;
	}
}
