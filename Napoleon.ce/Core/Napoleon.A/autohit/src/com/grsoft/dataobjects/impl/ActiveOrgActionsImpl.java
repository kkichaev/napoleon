package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ActiveOrgActionItem;
import com.grsoft.dataobjects.ActiveOrgActions;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.ActiveActionsDetail;
import com.grsoft.napoleon.documents.ActiveOrgActionsDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class ActiveOrgActionsImpl extends CreatableDocument<ActiveOrgActions> {

	@Override
	public void open(Context context) {
		ActiveActionsDetail.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		Date dt = Util.getDateTime();
		long r = find(orgId, dt);
		
		if( r != ExtrasConst.INVALID_ID )
			read(r);
		else {
			if( super.init(context, orgId, gpsCoord) ) {
				data.date = data.created;
				write();
			}
			r = getRowid();
		}
		
		return true;
	}
	
	@Override
	public long write() {
		long ret = super.write();
		ActiveOrgActionsDoc.instance().refreshDocSum(data.id);
		return ret;
	}
	
	static public long find(String orgId, Date d) {
		long ret = ExtrasConst.INVALID_ID;
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);
		String tn = DataObjectInfo.getInstance().getTableName(ActiveOrgActions.class);
		String condition = "id='" + orgId + "' AND date >= " + Long.toString(from) + " AND date < " + Long.toString(to);
		DbWriter.checkDBTable(getDataType(ActiveOrgActions.class));
		List<Long> ids = DbReader.readIds(tn, condition, null);
		
		if( ids.size() > 0 )
			ret = ids.get(0);
		return ret;
	}

	public void changeItem(String id) {
		if( !isEditable() )
			return;
		
		for(ActiveOrgActionItem i : data.items)
			if(i.id.equals(id)) {
				data.items.remove(i);
				write();
				return;
			}
		
		ActiveOrgActionItem oai = new ActiveOrgActionItem();
		oai.id = id;
		data.items.add(oai);
		write();
	}
}
