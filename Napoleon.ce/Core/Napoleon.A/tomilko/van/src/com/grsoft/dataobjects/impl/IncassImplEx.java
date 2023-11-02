package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GpsCoord;

public class IncassImplEx extends IncassImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		IncassEx ie = (IncassEx)data;
		ie.number = DocHelper.makeDocNumber(this);
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public boolean delete() {
		if( !super.delete() )
			return false;
		
		OrgHelper.refresh();
		return true;
	}

	@Override
	public long write() {
		long ret = super.write();
		DebtDoc.instance().refreshDocSum(data.id);
		return ret;
	}
	
	@Override
	public String getDescription(Context context) {
		return ((IncassEx)data).number.length() > 0 ? ((IncassEx)data).number :
				super.getDescription(context);
	}
}
