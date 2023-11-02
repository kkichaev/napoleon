package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.CellsAudit;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.StartAudit;
import com.grsoft.dataobjects.impl.CellsAuditImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

public class StartAuditHitching extends Hitching {
	
	CellsAuditImpl doc = new CellsAuditImpl();
	long curtime;
	
	public StartAuditHitching() {
		super(StartAudit.class, "StartAutomatRest");
		curtime = Util.getDateTime().getTime();
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		doc.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		StartAudit dobj = (StartAudit)rawObject.createDataObject(dataObject);
		
		CellsAudit ca = doc.getData();
		ca.created = new Date(curtime);
		ca.id = dobj.id;
		ca.date = dobj.date;
		ca.params = ParamState.ofExported;
		ca.items = dobj.items;
		
		curtime += 1000;
		
		doc.write();
	}
}
