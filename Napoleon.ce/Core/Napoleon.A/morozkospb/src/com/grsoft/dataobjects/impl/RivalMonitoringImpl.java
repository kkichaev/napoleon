package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.RivalMonitoring;
import com.grsoft.napoleon.RivalMonitoringDetail;

import android.content.Context;

public class RivalMonitoringImpl extends MonitoringImplBase<RivalMonitoring> {

	@Override
	public void open(Context context) {
		RivalMonitoringDetail.open(context, getRowid());
	}

	@Override
	protected String getItemID(long itemRowid) {
		RivalPriceImpl pi = new RivalPriceImpl();
		pi.read(itemRowid);
		pi.close();
		return pi.getData().id;
	}
}
