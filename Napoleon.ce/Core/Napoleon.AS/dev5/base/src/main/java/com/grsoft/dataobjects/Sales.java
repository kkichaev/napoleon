package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;

@TableInfo(name="sales", keyFields="created", indexes="number")
public class Sales extends Order {
	
	/**
	 * Код фирмы(Firm)
	 */
	public String supplyercode = "";

	public int useTax = 0;

	@SuppressLint("UseSparseArrays")
	public Map<Integer, Integer> makeTaxEntries() {
		Map<Integer, Integer> nds = new HashMap<Integer, Integer>();
		PriceImpl priceImpl = new PriceImpl();
		Price price = priceImpl.getData();
		
		for(OrderItem oitem: items){
			SalesItem sitem = (SalesItem)oitem;
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
