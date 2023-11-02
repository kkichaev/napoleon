package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchIncass;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.napoleon.documents.ArchIncassDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GpsCoord;

public class IncassImplEx extends IncassImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		IncassEx ie = (IncassEx)data;
		String table = DataObjectInfo.getInstance().getTableName(Incass.class);
		ie.number = DocHelper.makeDocNumber(this);
		DocHelper.saveDocNumber(table, ie.number);
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		ie.ido = oe.ido;
		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public boolean delete() {
		if( data.sum > 0 ) {
			ArchIncass ai = new ArchIncass();
			String table = DataObjectInfo.getInstance().getTableName(Incass.class);
			DbReader r = new DbReader();
			if( r.select(ai, table, "created="+data.created.getTime()) ) {
				DbWriter w = new DbWriter();
				ai.params = 0;
				w.insertRecord(ai);
				w.close();
				ArchIncassDoc.instance().refreshDocSum(data.id);
			}
		}
		
		if( !super.delete() )
			return false;
		
		OrgHelper.refresh();
		return true;
	}

	@Override
	public long write() {
		long ret = super.write();
		DebtDoc.instance().refreshDocSum(data.id);
		OrgHelper.refresh();
		return ret;
	}
	
	@Override
	public String getDescription(Context context) {
		return ((IncassEx)data).number.length() > 0 ? ((IncassEx)data).number :
				super.getDescription(context);
	}
}
