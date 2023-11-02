package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import com.grsoft.dataobjects.ActCost;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	// price=>org=>cost
	static HashMap<String, HashMap<String, ActCost>> costs = null;
	static long curDate = 0; 
	
	public static void clearCache() {
		costs = null;
	}
	
	static void loadCache(Date docDate) {
		if( costs != null || docDate == null || curDate == docDate.getTime())
			return;
		
		String docDateStr = Long.toString(docDate.getTime());
		
		costs = new HashMap<String, HashMap<String,ActCost>>();
		DataTraveler.travel(ActCost.class, new DataTraveler.Travel<ActCost>() {

			@Override
			public boolean travel(DataTraveler<ActCost> item) {
				HashMap<String, ActCost> cv = null;
				String id = item.data.id;
				if( costs.containsKey(id) == false ) {
					cv = new HashMap<String, ActCost>();
					costs.put(id, cv);
				} else
					cv = costs.get(id);
				
				cv.put(item.data.idOrg, item.data);
				item.data = new ActCost();
				return true;
			}
		}, "start <= " + docDateStr + " and end >=" + docDateStr );
	}
	
	public ActCost getActCost(Price p, Document<?> doc) {
		if(costs == null)
			loadCache(doc.getDate());
		
		if( doc != null && costs != null) {
			HashMap<String, ActCost> cv = costs.get(p.id);
			if( cv != null ) {
				ActCost cost = cv.get(doc.getId());
				if( cost != null )
					return cost;
				cost = cv.get("");
				if( cost != null )
					return cost;
			}
		}
		
		return null;
	}
	
	public int getProtocolCost(Price p, Document<?> doc) {
		int result = 0;
		if( doc != null ) {
			int sumType = Features.COST_MANAGER.getCostIndex(doc.getId());
			if( sumType >= 0 )
				result = Features.COST_MANAGER.getCost(p.id, sumType);
		}
		
		return result;
	}
	
	public int getStdCost(Price p) { return p.cost.get(0).cost; } 
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = 0;
		
		if(doc instanceof OrderImpl){
		   OrderItemEx item = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
		   
		   if(item != null){
			   int idx = Consts.INVALID_ID;
			   
			    if(item.useact == 1) {
					ActCost ac = getActCost(p, doc);
					if( ac != null ){
						result = ac.cost;
						idx = PriceCountEx.ACTION_COST_TYPE;
					}
				}
			    
			    if(idx == Consts.INVALID_ID){
			    	result = getProtocolCost(p, doc);
			    	
			    	if(result > 0 )
			    		idx = PriceCountEx.PROTOCOL_COST_TYPE;
			    	else{
			    		idx = PriceCountEx.BASE_COST_TYPE;
			    		result = getStdCost(p);
			    	}
			    }
			    
			    item.costidx = idx;
			    
		   }else{
			   ActCost ac = getActCost(p, doc);
			   if( ac != null )
				   result = ac.cost;
			   
			    if( result == 0 ) 
			    	result = getProtocolCost(p, doc);

			    result = result > 0 ? result : getStdCost(p);
		   }
		}else
			result = super.getItemCost(p, doc);
		
		return result;
	}
}
