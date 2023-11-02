package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ReturnResponse;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.util.GpsCoord;

import android.content.Context;

import java.util.List;

public class ReturnImplEx extends ReturnImpl {
	ReturnResponse response = null;

	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
	
	@Override public void editProperties(Context context, boolean isOldOrder) {}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		super.init(context, orgId, coord);
		Warehouse.open(context, this, false);
		return false;
	}

	@Override
	public String getDescription(Context context) {
		readResponse();
		if(response != null)
			return response.rejected() ? context.getString(R.string.rejected) :
				context.getString(R.string.accepted);
		return super.getDescription(context);
	}

	void readResponse() {
		if(response == null) {
			List<ReturnResponse> rr = DbReader.fetch(ReturnResponse.class, "created=" + Long.toString(data.created.getTime()));
			if(rr.size() > 0) {
				response = rr.get(0);
			}
		}
	}
}
