package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.AgentPlanNew;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PlanChanges;
import com.grsoft.dataobjects.PlanGroups;
import com.grsoft.dataobjects.PlanQtyData;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Util;

public class AgentPlanNewImpl extends DbObject<AgentPlanNew> {
	
	public static Map<OrgDog, List<MatrixItemEx>> getPlansAsMatrix(final Date planDate, OrgEx org) {
		 final Map<OrgDog, List<MatrixItemEx>> ret = new HashMap<OrgDog, List<MatrixItemEx>>();
		 
		DataTraveler.travel(OrgDog.class, new DataTraveler.Travel<OrgDog>(true) {

			@Override
			public boolean travel(DataTraveler<OrgDog> item) {
				HashSet<String> items = getPlanItems(planDate, item.data.firm);
				if(items.size() > 0) {
					HashSet<String> used = new HashSet<String>();
					List<MatrixItemEx> mtx = new ArrayList<MatrixItemEx>();
					for(String s : items) {
						if(used.contains(s))
							continue;
						used.add(s);
						MatrixItemEx mie = new MatrixItemEx();
						mie.id = s;
						mtx.add(mie);
					}
					ret.put(item.data, mtx);
				}
				return true;
			}
		},  "ido='" + ((OrgEx)org).ido + "'");
		 
		 
		 return ret;
	}
	
	public static HashSet<String> getPlanItems(Date planDate, String firmCode) {
		final HashSet<String> result = new HashSet<String>();
		
		String where = "isMonthly=0";
		String filter = "";
		if(firmCode != null && firmCode.length() > 0 )
			filter += "firm='" + firmCode + "'";
		if( planDate != null ) {
			if( filter.length() > 0 )
				filter += " and ";
			filter += "date>=" + Long.toString(Util.getDayStart(planDate).getTime()) + " and date<=" + 
					Long.toString(Util.getDayEnd(planDate).getTime());
		}
		if( filter.length() > 0 )
			where += " and " + filter;
		
		DataTraveler.travel(AgentPlanNew.class, new DataTraveler.Travel<AgentPlanNew>() {

			@Override
			public boolean travel(DataTraveler<AgentPlanNew> item) {
				for(AgentPlanItem ai : item.data.items)
					result.add(ai.id);
				return true;
			}
		}, where);
		
		return result;
	}
	
	static HashMap<String, Integer> loadChanges(String where) {
		final HashMap<String, Integer> ret = new HashMap<String, Integer>();
		
		DataTraveler.travel(PlanChanges.class, new DataTraveler.Travel<PlanChanges>() {

			@Override
			public boolean travel(DataTraveler<PlanChanges> item) {
				ret.put(item.data.id, item.data.qty);
				return true;
			}
		}, where);
		
		return ret;
	}
	
	public static Map<String, PlanQtyData> getPlans(String firmCode, Date date) {
		final PriceImpl pi = new PriceImpl();
		
		final HashMap<String, PlanQtyData> ret = new HashMap<String, PlanQtyData>();
		final HashMap<String, PlanGroups> groups = new HashMap<String, PlanGroups>();
		
		String where = "isMonthly=0";
		String filter = "";
		if(firmCode != null && firmCode.length() > 0 )
			filter += "firm='" + firmCode + "'";
		if( date != null ) {
			if( filter.length() > 0 )
				filter += " and ";
//			filter += "date=" + Long.toString(Util.getDayStart(date).getTime());
			filter += "date>=" + Long.toString(Util.getDayStart(date).getTime()) + " and date<=" + 
					Long.toString(Util.getDayEnd(date).getTime());
		}
		if( filter.length() > 0 )
			where += " and " + filter;

		DataTraveler.travel(PlanGroups.class, new DataTraveler.Travel<PlanGroups>() {

			@Override
			public boolean travel(DataTraveler<PlanGroups> item) {
				groups.put(item.data.id, item.data);
				item.data = new PlanGroups();
				return true;
			}
		}, null);

		final HashMap<String, Integer> changes = loadChanges(filter);
		
		DataTraveler.travel(AgentPlanNew.class, new DataTraveler.Travel<AgentPlanNew>() {

			@Override
			public boolean travel(DataTraveler<AgentPlanNew> item) {
				for(AgentPlanItem ai : item.data.items) {
					PriceEx p = (PriceEx) pi.getData();
					p.id = ai.id;
					pi.read();
					
					String group = null;
					int inPack = p.qtyInPack;
					PlanGroups pg = groups.get(ai.id);
					if( pg != null ) {
						inPack = pg.inPack;
						group = pg.group;
					}
					
					Integer chQty = changes.get(ai.id);
					ret.put(ai.id, new PlanQtyData(ai.qty, chQty == null ? 0 : chQty, group, inPack));
				}
				return true;
			}
		}, where);

		pi.close();		
		return ret;
	}
}
