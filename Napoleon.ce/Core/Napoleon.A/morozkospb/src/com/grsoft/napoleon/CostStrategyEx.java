package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceCostItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgCostImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	static HashMap<String, Integer> cost = null;
	//public static HashMap<String, OrgCost> orgCost = null;
	
	static String curCostType = "";
	
	public static void resetCache() {
		cost = null;
		//orgCost = null;
	}
	
	void loadCost(String costType) {
		if(cost == null || curCostType.equals(costType) == false) {
			String ct = costType; 
			if(ct.length()== 0)
				ct = getFirstCostType();
			cost = new HashMap<String, Integer>();
			DataTraveler.travel(PriceCost.class, new DataTraveler.Travel<PriceCost>() {

				@Override
				public boolean travel(DataTraveler<PriceCost> item) {
					for(PriceCostItem pci : item.data.items)
						cost.put(pci.id, pci.cost);
					return false;
				}
			}, "idPrice='" + ct + "'");
		
			curCostType = costType;
		}
	}

//	void loadOrgCost(){
//		if(orgCost == null){
//			orgCost = new HashMap<String, OrgCost>();
//			
//			DataTraveler.travel(OrgCost.class, new DataTraveler.Travel<OrgCost>(true) {
//
//				@Override
//				public boolean travel(DataTraveler<OrgCost> item) {
//					String key = getOrgCostKey(item.data.ido, item.data.id); 
//					orgCost.put(key, item.data);
//					return true;
//				}
//			}, null);
//		}
//	}
	
	
	public static  OrgCost readOrgCost(String ido, String id) {
		OrgCost result = null;
		
		OrgCostImpl impl = new OrgCostImpl();
		impl.getData().id = id;
		impl.getData().ido = ido;
		
		if (impl.read())
			result = impl.getData();
		
		impl.close();
		
		return result;
	}
	
//	public static String getOrgCostKey(String ido, String id) {
//		return String.format("%s\t%s", ido, id);
//	}
//	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		
		
		Integer curCost = null;
		
		OrgCost o = readOrgCost(doc.getId(), p.id);
		
		if (o != null)
			return o.cost;
		
		String costType = "";
		if( doc instanceof OrderImplEx) {
			costType = ((OrderEx)doc.getData()).prcType;
		}
		
		loadCost(costType);
		curCost = cost.get(p.id);
		return curCost == null ? 0 : curCost;
	}
	
	private String getFirstCostType() {
		PriceTypeFinder ptf = new PriceTypeFinder();
		DataTraveler.travel(PriceCost.class, ptf, "", "\"index\"");
		return ptf.ret;
	}

	public static boolean hasOrgText(Document<?> document, String id) {
		//String key = getOrgCostKey(document.getId(), id);
		OrgCost oc = readOrgCost(document.getId(), id);
		return oc != null && oc.text != null && oc.text.trim().length() > 0;
	}
	
	public static String getOrgText(Document<?> document, String id) {
//		String key = getOrgCostKey(document.getId(), id);
//		OrgCost oc = orgCost.get(key);
		OrgCost oc = readOrgCost(document.getId(), id);
		return oc != null && oc.text != null ? oc.text : "";
	}
}

class PriceTypeFinder extends DataTraveler.Travel<PriceCost> {
	public String ret;
	public PriceTypeFinder() { ret = ""; }
	
	@Override
	public boolean travel(DataTraveler<PriceCost> item) {
		ret = item.data.idPrice;
		return false;
	}
}
