package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.impl.PriceImpl;

public class DeliveryPrint extends Delivery {
	public String supplyercode;

	public Map<Integer, Integer> makeTaxEntries() {
		Map<Integer, Integer> nds = new HashMap<Integer, Integer>();
		PriceImpl priceImpl = new PriceImpl();
		PricePrint price = (PricePrint) priceImpl.getData();
		
		for(DeliveryItem oitem: items){
			DeliveryItemPrint sitem = (DeliveryItemPrint)oitem;
			price.id = sitem.id;				
			if (priceImpl.read()){
				
				int tax = price.tax1;
				int isumtax = sitem.taxSum;
				
				if (tax > 0)
					if (nds.containsKey(tax))
						nds.put(tax, nds.get(tax) + isumtax);
					else
						nds.put(tax, isumtax);
				
				
			}
		}
		priceImpl.close();		
		return nds;
	}
	
}
