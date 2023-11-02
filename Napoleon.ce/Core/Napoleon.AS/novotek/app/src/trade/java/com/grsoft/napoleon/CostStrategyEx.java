package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org2Ex;
import com.grsoft.dataobjects.OrgSegmentItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.PriceSegmentItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SkladHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

public class CostStrategyEx extends CostStrategy{
	private static Map<String,Set<String>> orgSegments = new HashMap<String,Set<String>>();
	private static Map<String,Map<String, Integer>> discHash = null; 
	
	public static void refresh(String id) {
		if(discHash == null) {
			discHash = new HashMap<String, Map<String,Integer>>();
			
			DataTraveler.travel(Discount.class, new DataTraveler.Travel<Discount>() {

				@Override
				public boolean travel(DataTraveler<Discount> item) {
					if (!discHash.containsKey(item.data.orgSgmID))
						discHash.put(item.data.orgSgmID, new HashMap<String, Integer>());
					
					if (!discHash.get(item.data.orgSgmID).containsKey(item.data.priceSgmID))
						discHash.get(item.data.orgSgmID).put(item.data.priceSgmID, item.data.discount);
					
					return true;
				}
			}, null);
		}
		
		if(!orgSegments.containsKey(id)) {
			OrgImpl oi = new OrgImpl();
			Org2Ex org = (Org2Ex) oi.getData();
			org.id = id;
			oi.read();
			oi.close();
			Set<String> set = new HashSet<String>();
			
			for(OrgSegmentItem i : org.segments)
				set.add(i.sgmid);
			
			orgSegments.put(id, set);
		}
	}

	
	@Override
	public long getCostInt(Price p, Document<?> doc, int sumType) {
		int result = (int) super.getCostInt(p, doc, sumType);
		
		if(doc != null && DocType.getCurDoc() == OrderDoc.instance() && 
				doc.getRowid() != ExtrasConst.INVALID_ROWID &&
				SkladHelper.useDiscount(((OrderEx)doc.getData()).whCode)) {
			
			refresh(doc.getId());
			Integer maxDisc = null;
			
			for(String o : orgSegments.get(doc.getId())) {
				for(PriceSegmentItem i : ((Price2Ex)p).segments) {
					if(discHash.containsKey(o) && discHash.get(o).containsKey(i.sgmid)) {
						if (maxDisc == null)
							maxDisc = discHash.get(o).get(i.sgmid);
						else
							maxDisc = Math.max(maxDisc, discHash.get(o).get(i.sgmid));
					}
				}
			}
			
			if (maxDisc == null)
				maxDisc = 0;
			
			result = (int) costWithDiscount(result, maxDisc, Consts.DISCOUNT_SCALE);
		}
		
		return result;
	}
	
	public static Set<String> getOrgSegments(String id){
		refresh(id);
		return orgSegments.get(id);
	}
	
	public static boolean haveDiscount(String orgSgmId, String prcSgmId ) {
		return discHash.containsKey(orgSgmId) && discHash.get(orgSgmId).containsKey(prcSgmId);
	}
	
	public static void resetCash() {
		discHash = null;
		orgSegments.clear();
	}
}
