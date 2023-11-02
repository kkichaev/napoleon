package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.napoleon.DistribEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class DistribImpl extends CreatableDocument<Distrib> {

	@Override
	public void open(Context context) {
		DistribEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		long rc = find(orgId, Util.getDateTime());
		if( rc != ExtrasConst.INVALID_ROWID ) {
			read(rc);
			return true;
		}
		return super.init(context, orgId, gpsCoord);
	}
	
	static public long find(String orgId, Date d) {
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);

		String where = "id='" + orgId + "' AND created >= " + Long.toString(from) + " AND created < " + Long.toString(to);
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Distrib.class), where, null);
		return ids.size() > 0 ? ids.get(0) : ExtrasConst.INVALID_ROWID;
	}
}
