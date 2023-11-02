package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderRequest;
import com.grsoft.napoleon.documents.CreatableDocument;


public class OrderRequestImpl extends CreatableDocument<OrderRequest> {

	@Override
	public void open(Context context) { }

	public static OrderRequest inflateDecision(long created){
		OrderRequest result = new OrderRequest();
		DbReader r = new DbReader();
		
		if(!r.select(result, result.getTableName(), String.format("[order]=%d", created), "[created] DESC"))
			result = null;
		
		r.close();
		
		return result;
	}
}
