package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.RivalPriceImpl;

import android.content.Context;

public class RivalDetailAdapter extends MonitoringDetailAdapter {

	RivalPriceImpl rpi = new RivalPriceImpl();
	
	public RivalDetailAdapter(Context context) {
		super(context);
	}
	
	@Override
	protected String getName(String id) {
		rpi.read("id", id);
		return rpi.getData().name;
	}

}
