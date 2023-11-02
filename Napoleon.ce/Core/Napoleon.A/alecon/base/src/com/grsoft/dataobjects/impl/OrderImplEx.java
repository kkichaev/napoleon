package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.util.GpsCoord;

public class OrderImplEx extends OrderImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		// абанов сказал ненадо показывать это окошко!
//		OrgSumImpl os = new OrgSumImpl();
//		OrgSum sum = os.getData();
//		sum.id = orgId;
//		sum.type = OrderDoc.instance().getName();
//		if( os.read() && sum.sum > 0 ) {
//			String text = context.getResources().getString(R.string.org_balance);
//			text += " " + Util.IntToScaleStr(sum.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
//			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//		}
//		os.close();
		return super.init(context, orgId, coord);
	}
}
