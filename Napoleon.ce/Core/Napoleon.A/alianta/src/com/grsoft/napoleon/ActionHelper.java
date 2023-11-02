package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.ActionItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Util;

public class ActionHelper {
	
	static String id = "";
	static Date date = new Date();
	static Map<String, List<ActionData>> data = new HashMap<String, List<ActionData>>();
	
	public static class ActionData {
		public ActionData(Action data, ActionItem ai) {
			start = data.start;
			end = data.end;
			text = data.name;
			isManual = data.isManual;
			discount = ai.discount;
			id = data.id;
		}
		public String id = "";
		public Date start = new Date();
		public Date end = new Date();
		public String text = "";
		public int isManual;
		public int discount;
		
		public String makeText() {
			if(isManual > 0)
				return text;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			return String.format("%s с %s по %s", text, sdf.format(start), sdf.format(end));
		}
	}
	
	public static void resetCache() {
		id = "";
		data.clear();
	}
	
	static void load(String orgId, Date date) {
		date = Util.getDayStart(date);
		if(!id.equals(orgId) || !ActionHelper.date.equals(date)) {
			id = orgId;
			ActionHelper.date = date;
			
			data.clear();
			OrgImpl oi = new OrgImpl();
			final OrgEx oe = (OrgEx)oi.getData();
			oe.id = orgId;
			oi.read();
			oi.close();
			
			String dateStr = Long.toString(date.getTime());
			String where = "\"start\" <= " + dateStr + " and \"end\" >= " + dateStr;
			
			DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>() {

				@Override
				public boolean travel(DataTraveler<Action> item) {
					if(item.data.canApply(oe)) {
						for(ActionItem ai : item.data.items) {
							List<ActionData> ad = data.get(ai.id);
							if(ad == null) {
								ad = new ArrayList<ActionHelper.ActionData>();
								data.put(ai.id, ad);
							}
							ad.add(new ActionData(item.data, ai));
						}
					}
					return true;
				}
			}, where);
		}
	}
	
	public static List<ActionData> getActions(String orgId, Date date, String itemId) {
		if(orgId == null || date == null)
			return null;
		load(orgId, date);
		return data.get(itemId);
	}
	
	public static List<String> getActionItems(String orgId, Date date) {
		List<String> ret = new ArrayList<String>();
		if(orgId == null || date == null)
			return ret;
		load(orgId, date);
		ret.addAll(data.keySet());
		return ret;
	}
}
