package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Report;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class ReportHitching extends Hitching {

	public static final  String REPORT_ID = "DEBT";

	public ReportHitching() {
		super(Report.class);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Report dobj = (Report) rawObject.createDataObject(dataObject);
//		dobj.id = REPORT_ID;
		dbProxy.insertRecord(dobj);
	}

}
