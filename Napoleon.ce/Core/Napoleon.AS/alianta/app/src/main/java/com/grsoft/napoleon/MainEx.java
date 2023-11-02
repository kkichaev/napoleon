package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.GoodsAnalogs;
import com.grsoft.dataobjects.LoadedOrders;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	
	Set<String> changedOrders = new HashSet<String>();

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		DbWriter.checkDBTable(GoodsAnalogs.class);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// используем для показа просроценных лицензий
		changedOrders.clear();

		List<Org> orgs = DbReader.fetch(Org.class, "license < " + Long.toString(new Date().getTime()));
		for(Org o : orgs) {
			changedOrders.add(o.id);
		}

//		Date sdate = Util.getDayStart(new Date());
//		String where = "created > " + Long.toString(sdate.getTime()) + " and created < " +
//				Long.toString(sdate.getTime() + 24 * 3600 * 1000);
//
//		Map<Date, LoadedOrders> lo = LoadedOrders.get(where);
//		OrderImplEx oi = new OrderImplEx();
//		Order doc = oi.getData();
//		for(LoadedOrders i : lo.values()) {
//			doc.created = i.created;
//			if(oi.read() && !i.isEqualToOrder(doc))
//				changedOrders.add(doc.id);
//		}
	}
	
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		if(changedOrders.contains(org.id)) {
			((TextView)view.findViewById(R.id.tvOrgSum)).setTextColor(Color.RED);
		}
	}
	
}
