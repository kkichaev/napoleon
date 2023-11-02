package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.ActionData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.TradeAction;
import com.grsoft.dataobjects.TradeActionItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Util;

public class CostStrategyEx extends CostStrategy {
	static OrgEx oe;
	static Date actDate = null;
	static Map<String, ActionData> actions = new HashMap<String, ActionData>();
	
	public static void resetCache() {
		oe = null;
		actDate = null;
		actions.clear();
	}
	
	void load(String orgId, Date docDate) {
		if(docDate == null) {
			resetCache();
			return;
		}
		if(oe == null || oe.id.equals(orgId) == false) {
			OrgImpl oi = new OrgImpl();
			oe = (OrgEx)oi.getData();
			oe.id = orgId;
			oi.read();
			oi.close();
			actDate = null;
		}
		Date curDay = Util.getDayStart(docDate);
		if(actDate == null || !actDate.equals(curDay)) {
			actDate = curDay;
			actions.clear();
			
			Date cDay = new Date(curDay.getTime() - (24 * 2600 * 1000));
			String prevDayStr = Long.toString(cDay.getTime());
			cDay = new Date(curDay.getTime() + (24 * 2600 * 1000));
			String nextDayStr = Long.toString(cDay.getTime());
			String where = "[start] <= " + nextDayStr + " and [end] >= " + prevDayStr; 
			DataTraveler.travel(TradeAction.class, new DataTraveler.Travel<TradeAction>() {

				@Override
				public boolean travel(DataTraveler<TradeAction> item) {
					if(item.data.contains(oe)) {
						for(TradeActionItem i : item.data.items)
							actions.put(i.id, new ActionData(item.data, i));
					}
					return true;
				}
				
			}, where);
		}
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		if(doc instanceof OrderImplEx) {
			ActionData ad = getActionData(doc, p.id);
			if(ad != null)
				return ad.cost;
			
			if(oe != null) {
				int ct = Features.COST_MANAGER.getCostIndex(oe.id);
				if(ct >= 0) {
					int cost = Features.COST_MANAGER.getCost(p.id, ct);
					if( cost > 0)
						return cost;
				}
			}
		}
		return getPriceCost(p, sumType, doc);
	}
	
	public ActionData getActionData(Document<?> doc, String itemId) {
		load(doc.getId(), doc.getDate());
		return actions.get(itemId);
	}
}

