package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.MoneyProxy;
import com.grsoft.napoleon.MoneyProxyForm;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class MoneyProxyImpl extends CreatableDocument<MoneyProxy>{

	@Override
	public CreatableDocument<MoneyProxy> copy() {
		MoneyProxyImpl copy = null;
		
		if( rowid != ExtrasConst.INVALID_ID ) {
			copy = new MoneyProxyImpl();
			copy.read(rowid);

			copy.data.date = Util.getDateTime();
			copy.data.params = 0;
			copy.rowid =  ExtrasConst.INVALID_ID;
			copy.write();
		}
		return copy;
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		super.init(context, orgId, coord);
		open(context);
		return false;
	}

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) {
		return (isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			""; 
}

	@Override
	public String getId() { return data.id;	}

	@Override
	public void open(Context context) { MoneyProxyForm.open(context, this); }

	@Override
	public long sum() { return data.sum; }

}
