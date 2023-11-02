package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderDecision;
import com.grsoft.napoleon.documents.CreatableDocument;


public class OrderDecisionImpl extends CreatableDocument<OrderDecision> {

	@Override
	public void open(Context context) { }
	
	public static OrderDecision getDecision(Date order){
		OrderDecision result = new OrderDecision();
		DbReader reader = new DbReader();
		
		if (!reader.select(result, result.getTableName(), String.format("[order]=%d", order.getTime())))
				result = null;
		
		return result;
	}

}
