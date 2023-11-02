package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDocEx extends OrderDoc {
	private Map<String, Integer> sums = new HashMap<String, Integer>();
	
	public static void init() {
		instance = new OrderDocEx();
	}

	private OrderDocEx() {
		super("Заявки", "Order", OrderImpl.class);
	}

	@Override
	protected CharSequence getValueFromOrgSum(OrgSumImpl orgSumImpl) {
		String id = orgSumImpl.getData().id;
		if(!sums.containsKey(id))
			refreshDocSum(id);
		
		return Util.IntToScaleStr(sums.get(id), Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date start = calendar.getTime();
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		Date finish = calendar.getTime();
		
		StringBuilder where = new StringBuilder();
		where.append("created >=").append( start.getTime()).append(" and created <").append(finish.getTime());
		int sum = 0;
		
		DocList list = docList(orgId, null, where.toString());
		for( int i=0; i<list.getCount(); i++ ){
			Document<?> d = list.get(i);
			if( d != null ) sum += d.sum();
		}
		
		list.close();
		
		sums.put(orgId, sum);
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count){
		int result = 0;
		
		for (int s : sums.values())
			result += s;
		
		updateTotalSum(activity, result, weight, count, R.id.tvTotalSum);
	}
}
