package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.LoadedOrders;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	
	Set<String> changedOrders = new HashSet<String>();
	
	@Override
	protected void onResume() {
		super.onResume();

		changedOrders.clear();

		Date sdate = Util.getDayStart(new Date());
		String where = "created > " + Long.toString(sdate.getTime()) + " and created < " + 
				Long.toString(sdate.getTime() + 24 * 3600 * 1000);
		
		Map<Date, LoadedOrders> lo = LoadedOrders.get(where);
		OrderImplEx oi = new OrderImplEx();
		Order doc = oi.getData();
		for(LoadedOrders i : lo.values()) {
			doc.created = i.created;
			if(oi.read() && !i.isEqualToOrder(doc))
				changedOrders.add(doc.id);
		}
	}
	
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		if(changedOrders.contains(org.id)) {
			((TextView)view.findViewById(R.id.tvOrgSum)).setTextColor(Color.RED);
		}
	}
	
}
