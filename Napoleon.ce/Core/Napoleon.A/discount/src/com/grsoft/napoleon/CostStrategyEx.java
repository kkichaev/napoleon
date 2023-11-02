package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SkladItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static String whCode = "";
	static HashMap<String, SkladItem> data = null;
	
	public static void clearCache() { data = null; }
	
	
	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		int res = 0;
		if( doc instanceof OrderImplEx) {
			loadCache(((OrderEx)doc.getData()).whCode);
			SkladItem s = data.get(p.id);
			if(s != null && s.cost.size() > sumType && sumType >= 0) 
				res = s.cost.get(sumType).cost;
		}
		
		if(res == 0)
			res = super.getPriceCost(p, sumType, doc);
		
		return res;
	}
	
	public static HashMap<String, SkladItem> getSkaldData(String whId) {
		loadCache(whId);
		return data;
	}


	private static void loadCache(String whId) {
		if(data == null || whCode.equals(whId) == false) {
			data = new HashMap<String, SkladItem>();
			DataTraveler.travel(SkladItem.class, new DataTraveler.Travel<SkladItem>(true) {

				@Override
				public boolean travel(DataTraveler<SkladItem> item) {
					data.put(item.data.id_i, item.data);
					return true;
				}
			}, "id='" + whId +"'");
			
			whCode = whId;
		}
	}
}
