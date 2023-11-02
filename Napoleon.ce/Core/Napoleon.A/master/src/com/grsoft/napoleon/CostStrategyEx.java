package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgAction;
import com.grsoft.dataobjects.OrgActionItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Util;

public class CostStrategyEx extends CostStrategy {
	
	static OrgAction actions;
	static HashMap<String, Integer> items;
	static Date actionDate;
	
	public static void clearCach() { 
		actions = null;
	}
	
	void loadActions(String id) {
		if(actions == null || actions.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = id;
			oi.read();
			oi.close();
			
			String where = "ido='" + oe.ido + "' or id='" + oe.id + "'";
			
			actions = new OrgAction();
			DataTraveler.travel(OrgAction.class, new DataTraveler.Travel<OrgAction>() {

				@Override
				public boolean travel(DataTraveler<OrgAction> item) {
					actions = item.data;
					return item.data.id.length() > 0 ? false : true;
				}
			}, where);
			actions.id = id;
			
			actionDate = null;
			items = null;
		}
	}
	
	public HashMap<String, Integer> getActionItems(String id, Date date) {
		loadActions(id);
		date = Util.getDayStart(date);
		if(items == null || actionDate == null || actionDate.compareTo(date) != 0) {
			
			items = new HashMap<String, Integer>();
			actionDate = date;
			
			for(OrgActionItem oai : actions.items) {
				if( oai.start.compareTo(date) <= 0 && oai.end.compareTo(date) >= 0 ) {
					items.put(oai.id, oai.cost);
				}
			}
			
		}
		
		return items;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc != null && doc.getDate() != null) {
			HashMap<String, Integer> act = getActionItems(doc.getId(), doc.getDate());
			Integer ci = act.get(p.id);
			if(ci != null)
				return ci;
		}
		return super.getItemCost(p, doc);
	}
}
