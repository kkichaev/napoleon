package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmRozduhov;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc instanceof ReturnImpl) {
			ReturnItemEx re = (ReturnItemEx) ((ReturnImpl)doc).findItem(p.id);
			if(re != null)
				return re.cost;
		}
		if( ! (doc instanceof OrderImplEx) ) 
			return super.getItemCost(p, doc);
		
		FirmRozduhov f = ((OrderImplEx)doc).getFirm();
		int index = 0;
		if( f.cost < p.cost.size() )
			index = f.cost;
		return p.cost.get(index).cost;
	}
}
