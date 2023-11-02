package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Discs;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;


public class DiscountHelper {
	private static List<Discs> discsCashe = null;
	
	public static void init(String id){
		discsCashe = new ArrayList<Discs>();
		
		DataTraveler.travel(Discs.class, new DataTraveler.Travel<Discs>() {
			@Override
			public boolean isDataNewInstance() { return true;	}
			@Override
			public boolean travel(DataTraveler<Discs> item) {
				discsCashe.add(item.data);
				return true;
			}}, "(id='"+id + "' OR id='') and type<=" + getTypeCount());
		
		Collections.sort(discsCashe, new Comparator<Discs>() {

			@Override
			public int compare(Discs lhs, Discs rhs) {
				int result = rhs.id.length() - lhs.id.length();
				
				if(result == 0)
					result = lhs.type - rhs.type;
				
				return result;
			}});
	}
	
	private static Discs getItemDisc(String id, PriceEx p){
		if(discsCashe == null)
			init(id);
		
		for(Discs d: discsCashe){
			switch(d.relation){
			case Discs.REL_ITEM:
				if(p.id.equals(d.idItem))
					return d;
			case Discs.REL_MFROWR:
				if(p.idMfr.equals(d.idMfr) && p.idOwr.equals(d.idOwr))
					return d;
			case Discs.REL_MFR:
				if(p.idMfr.equals(d.idMfr) && d.idOwr.equals(""))
					return d;
			case Discs.REL_OWR:
				if(p.idMfr.equals(d.idOwr) && d.idMfr.equals(""))
					return d;
			}
		}
		
		return null;
	}
	
	public static int getMaxDiscount(String id, PriceEx p){
		int result = getOrgDisc(id);
		Discs d = getItemDisc(id, p);
		
		if(d != null){
			if (d.mindisc == 1)
				result = Math.min(result,d.maxdisc);
			else
				result = d.maxdisc;
		}

		return result;
	}

	protected static int getOrgDisc(String id) {
		OrgImpl org = new OrgImpl();
		org.read("id", id);
		return ((OrgEx)org.getData()).discount;
	}
	
	public static int calcDisc(int cost, int disc){
		return cost - (int)((long)cost * disc + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE);
	}
	
	private static int getTypeCount(){
		int result = 0;
		final String key = " ол“ипов—кидок“овара";
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if(cfg.getValue(sb, key))
			try{
				result = Integer.parseInt(sb.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		
		return result;
	}
}
