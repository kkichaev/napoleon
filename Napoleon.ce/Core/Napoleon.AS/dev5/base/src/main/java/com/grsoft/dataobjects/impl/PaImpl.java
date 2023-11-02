package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.Calendar;

import android.content.Context;

import com.grsoft.dataobjects.Pa;
import com.grsoft.dataobjects.Pko;
import com.grsoft.napoleon.PaInfo;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class PaImpl extends CreatableDocument<Pa> {

	@Override
	public void open(Context context) {
		PaInfo.open(context, getRowid());
	}

	@Override
	public long sum() {
		return data.sum;
	}
	
	@Override
	public String getDescription(Context context) {
		return data.number;
	}
	
	@Override public String getNumber() { return data.number; }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Util.getDate());
		final int PERIOD_VALUE_IN_DAYS = 5;
		calendar.add(Calendar.DAY_OF_MONTH, PERIOD_VALUE_IN_DAYS);
		data.period = calendar.getTime();
		return super.init(context, orgId, gpsCoord);
	}
	
	public static PaImpl initFromPay(PkoImplBase<? extends Pko> pay, GpsCoord location) {
		PaImpl ret = new PaImpl();
		Pa dest = ret.getData();
		Pko src = pay.getData();
		
		dest.doccreated = src.created;
		dest.docnumber = src.number;
		
		if (!ret.read()){
			ret.init(null, src.id, location);

			dest.number = src.number;
			dest.sum = src.sum;
			dest.supplyercode = src.supplyercode;
			
			PaDoc.instance().refreshDocSum(src.id);
			ret.write();
		}
		
		ret.close();
		return ret;
	}
}
