package com.grsoft.ads.dataobjects.impl;

import android.content.Context;

import com.grsoft.ads.database.Return;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;

public class ReturnImpl extends CreatableDocument<Return> {

	@Override
	public void open(Context context) {
	}
	
	public void init(Order order){
		data.date = Util.getDateTime();
		data.created = order.created;
		write();
		close();
	}
}

