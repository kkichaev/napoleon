package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import com.grsoft.dataobjects.ActCost;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgCostTypes;
import com.grsoft.dataobjects.OrgProtocolCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class CostStrategyEx extends CostStrategy {
	
	// price=>org=>[cost]
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
		DataTraveler.travel(ActCost.class, new DataTraveler.Travel<ActCost>(true) {

			@Override
			public boolean travel(DataTraveler<ActCost> item) {
				String id = item.data.id;
				HashMap<String, ActCost> cv = costs.get(id);
				if( cv == null ) {
					cv = new HashMap<String, ActCost>();
					costs.put(id, cv);
				}
				
				ActCost ac = cv.get(item.data.idOrg);
				if(ac == null)
					cv.put(item.data.idOrg, item.data);
				return true;
			}
		}, "start <= " + docDateStr + " and end >=" + docDateStr, "priority asc" );
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
	
	public OrgProtocolCost getProtocolCost(Price p, Document<?> doc) {
		OrgProtocolCost result = null;
		if( doc != null ) {
			CostFinder cf = new CostFinder(doc.getDate(), p.id);
			DataTraveler.travel(OrgCostTypes.class, cf, "idOrg='" + doc.getId() + "'", "priority asc");
			
//			int sumType = Features.COST_MANAGER.getCostIndex(doc.getId());
//			if( sumType >= 0 )
//				result = Features.COST_MANAGER.getCost(p.id, sumType);
			
			result = cf.getCost();
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
			    	OrgProtocolCost opc = getProtocolCost(p, doc);
			    	if(opc != null)
			    		result = opc.cost;
			    	
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
			   
			    if( result == 0 ) {
			    	OrgProtocolCost ocs = getProtocolCost(p, doc);
			    	if(ocs != null)
			    		result = ocs.cost;
			    }

			    result = result > 0 ? result : getStdCost(p);
		   }
		}else
			result = super.getItemCost(p, doc);
		
		return result;
	}
}

class CostFinder extends DataTraveler.Travel<OrgCostTypes> {

	Date docDate;
	String id;
	OrgProtocolCost finded = null;
	
	static Date checkDate;
	
	public CostFinder(Date docDate, String id) {
		this.docDate = Util.getDayStart(docDate);
		this.id = id;
		
		if(checkDate == null) {
			checkDate = new Date(365 * 24 * 1000);
		}
	}
	
	@Override
	public boolean travel(DataTraveler<OrgCostTypes> item) {
		int cost = 0;
		if(isActive(item.data) ) {
			int sumType = Features.COST_MANAGER.getCostIndex(item.data.id);
			if( sumType >= 0 ) {
				cost = Features.COST_MANAGER.getCost(id, sumType);
				if(cost != 0) {
					finded = new OrgProtocolCost();
					DataObject.makeCopy(finded, item.data);
					finded.cost = cost;
				}
			}
		}
		return (cost == 0);
	}

	private boolean isActive(OrgCostTypes data) {
		return (data.start.compareTo(docDate) <= 0  && (data.end.compareTo(docDate) >= 0 || data.end.compareTo(checkDate) < 0));
	}
	
	
	public OrgProtocolCost getCost() { return finded; }
}
