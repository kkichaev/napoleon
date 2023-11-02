package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = 0; 
//		if(DocType.getCurDoc() == OrderDoc.instance())
//			result = ((PriceEx)p).promoCost;
		if(result == 0)
			result = super.getItemCost(p, doc);
			
		return result; 
	}
}
