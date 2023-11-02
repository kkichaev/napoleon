package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import com.grsoft.dataobjects.AgentSalesPlan;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.SalesDataItem;
import com.grsoft.util.Consts;

public class AgentSalesPlanImpl extends DbObject<AgentSalesPlan> {

	public static PlanList plans = null;
	public static DateSalesData data = null;
	
	public static void clearCache() {
		plans = null;
		data = null;
	}
	
	public static void refreshDocCache() {
		if( plans == null ) {
			plans = new PlanList();
			plans.load();
		}
		
		if( plans != null ) {
			data = new DateSalesData();
			data.load(plans.dateStart, plans.dateEnd);
		}
	}
	
	public static SalesDataItem getItemQty(String id) {
		if( plans == null ) {
			plans = new PlanList();
			plans.load();
		}
		
		if( data == null ) {
			data = new DateSalesData();
			data.load(plans.dateStart, plans.dateEnd);
		}
		
		int tq = 0;
		boolean plan = false;
		Date start = null, end = null;
		for(AgentSalesPlan cPlan : plans) {
			int qty = cPlan.getItemQty(id);
			if( qty >= 0 ) {
				if( start == null || start.compareTo(cPlan.dateStart) > 0 )
					start = cPlan.dateStart;
				// добавим 23:59:59
				Date endPaln = new Date(cPlan.dateEnd.getTime() + (3600 * 23 + 60 * 59 + 59) * 1000l);
				if( end == null || end.compareTo(endPaln) < 0 )
					end = endPaln;
				
				tq += qty;
				
				if(!plan) plan = true;
			}
		}

		if( plan ) {
			tq -= data.getSales(id, start, end);
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			p.id = id;
			pi.read();
			pi.close();
			
			tq = (int)((long)tq * Consts.QTY_SCALE / p.qtyInPack);
		}
		
		return plan ? new SalesDataItem(end, tq) : null; 
	}
}

class SalesData extends ArrayList<SalesDataItem> {
	private static final long serialVersionUID = 1L;
	
	int getSales(Date start, Date end) {
		int qty = 0;
		for(SalesDataItem i : this) {
			if( i.date.before(start) || i.date.after(end) )
				continue;
			qty += i.qty;
		}
		return qty;
	}
	
	void put(Date date, int qty) {
		long time = date.getTime();
		// оставим только день
		time = (time / (3600000l * 24)) * (3600000l * 24);
		
		Date cd = new Date(time);
		for(SalesDataItem i : this) {
			if( i.date.equals(cd)) {
				i.qty += qty;
				return;
			}
		}

		add(new SalesDataItem(cd, qty));
	}
}
