package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DVisit;
import com.grsoft.napoleon.dostavka.VisitEdit;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;


public class DVisitImpl extends DispatchDocImpl<DVisit> {

	@Override public void open(Context context) { VisitEdit.open(context, getRowid());}

	public boolean readOrCreate(Context context, DispatchImpl dispatch) {
		boolean result = false;
		data.created = dispatch.getData().visit;
		
		result = read();
		
		if(!result){
			result = init(context, dispatch, null, GPSUtilNew.getLastKnownLocation());
			
			if(result){
				dispatch.getData().visit = data.created;
				write();
			}
		}
		
		close();
		
		return result;
	}
	
	@Override
	public void postInit() {
		super.postInit();
		data.date = Util.getDateTime();
	}

}
