package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceCostInfo;
import com.grsoft.dataobjects.PriceCostItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class CostStrategyEx extends CostStrategy {
	static Map<String, List<PriceCostInfo>> cost = new HashMap<String, List<PriceCostInfo>>();
	static String priceType = "", orgId = "";
	static int discount = 0;
	
	public static void resetCach() {
		priceType = "";
		orgId = "";
		cost.clear();
	}
	
	static void load(String id) {
		if(orgId.equals(id)) 
			return;

		orgId = id;
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = id;
		oi.read();
		oi.close();
		discount = oe.discount;
		
		if(priceType.equals(oe.priceType)) 
			return;
		
		priceType = oe.priceType;
		cost.clear();
		
		DataTraveler.travel(PriceCost.class, new DataTraveler.Travel<PriceCost>(true) {

			@Override
			public boolean travel(DataTraveler<PriceCost> item) {
				for(PriceCostItem pci : item.data.items) {
					cost.put(pci.id, pci.items);
				}
				return true;
			}
		}, "id='" + priceType + "'");
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc == null)
			return 0;
		return getCost(p.id, doc.getId(), doc.getDate());
	}
	
	public static int getCost(String id, String orgId, Date docDate) {
		load(orgId);
		int ret = 0;
		List<PriceCostInfo> pci = cost.get(id);
		if(pci != null) {
			long checkDate = Util.getDayEnd(docDate).getTime();
			for(PriceCostInfo ci : pci) {
				long ciTime = ci.start.getTime();
				long ciEndTime = ci.finish.getTime();
				if(ciTime < checkDate && (ciEndTime >= checkDate || ciEndTime < 24 * 3600 * 1000)) {
					ret = ci.cost;
					break;
				}
			}
		}
		if(ret != 0 && discount != 0)
			ret = costWithDiscount(ret, discount, Consts.SUM_SCALE);
		return ret;
	}
}
