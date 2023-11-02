package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DispatchReturnsInfo;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class DispatchReturnsInfoImpl extends CreatableDocument<DispatchReturnsInfo> {

	@Override public void open(Context context) {}

	public static DispatchReturnsInfoImpl create(DWaybillDocument src, GpsCoord pos) {
		DispatchReturnsInfoImpl ret = new DispatchReturnsInfoImpl();
		
		ret.init(null, src.id, pos);
		
		ret.data.dispatch = src.dispatch;
		ret.data.routeItemId = src.routeItemId;
		ret.data.waybillDoc = src.created;
		
		return ret;
	}
	
	public static DispatchReturnsInfo find(DWaybillDocument src) {
		DispatchReturnsInfo ret = new DispatchReturnsInfo();
		DbReader r = new DbReader();
		String where = "waybillDoc=" + Long.toString(src.created.getTime());
		boolean read = r.select(ret, ret.getTableName(), where);
		r.close();
		
		return read ? ret : null;
	}
}
