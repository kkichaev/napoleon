package com.grsoft.dataobjects.impl;

import java.util.Date;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;

public class SalesFake extends SalesImplEx {
	@Override public void initDocNumber() {}
	
	@Override
	public void postInit() {
		super.postInit();
		
		Date created = getFakeDate();
		data.created = created;
	}
	
	public static  Date getFakeDate(){
		return new Date(0);
	}
	
	public static SalesFake getInstance(Context context){
		return getInstance(context, false);
	}
	
	public static SalesFake getInstance(Context context, boolean newInstance){
		SalesFake s = new SalesFake();
		s.data.created = getFakeDate();
		
		if(newInstance)
			s.getWriter().deleteRecord(s.getData(), s.getData().created.getTime());
		
		if (!s.read() || newInstance)
			s.init(context, "", GPSUtilNew.getLastKnownLocation());
		
		s.close();
		
		return s;
	}
	
	@Override public void editProperties(Context ctx, boolean isOldOrder) { }
}
