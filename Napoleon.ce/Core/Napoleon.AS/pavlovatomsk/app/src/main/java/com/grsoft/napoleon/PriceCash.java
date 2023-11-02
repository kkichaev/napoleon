package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Price;

public class PriceCash {
	private static Map<String, Price> price = new HashMap<String, Price>(); 
	
	public static void load() {
		price.clear();
		
		DataTraveler.travel(Price.class, new DataTraveler.Travel<Price>(true) {

			@Override
			public boolean travel(DataTraveler<Price> item) {
				price.put(item.data.id, item.data);
				return true;
			}
		}, null);
	}
	
	public static Price getPrice(String id) {
		return price.get(id);
	}
}
