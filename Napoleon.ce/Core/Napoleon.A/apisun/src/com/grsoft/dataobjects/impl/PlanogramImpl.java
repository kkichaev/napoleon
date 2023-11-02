package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Planogram;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.napoleon.PlanogramEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import android.content.Context;

public class PlanogramImpl extends CreatableDocument<Planogram> {

	@Override
	public void open(Context context) {
		PlanogramEdit.open(context, this);
	}
	
	static public long find(String orgId, Date d) {
		long ret = ExtrasConst.INVALID_ID;
		
		if(orgId != null && d != null){
			long from, to;
			from = Util.getDayStart(d).getTime();
			
			// перейдем на начало дня
//			from -= (from % (1000 * 3600 * 24));
			
			// начало следующего дня
			to = from + (1000 * 3600 * 24);
			String tn = DataObjectInfo.getInstance().getTableName(Planogram.class);
			String condition = "id='" + orgId + "' AND date >= " + Long.toString(from) + " AND date < " + Long.toString(to);
			DbWriter.checkDBTable(getDataType(Remnants.class));
			List<Long> ids = DbReader.readIds(tn, condition, null);
			
			if( ids.size() > 0 )
				ret = ids.get(0);
		}
		
		return ret;
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		Date dt = Util.getDateTime();
		long r = find(orgId, dt);
		boolean result = false;
		
		if( r != ExtrasConst.INVALID_ID )
			result = read(r);
		else {
			result = super.init(context, orgId, coord);
			r = getRowid();
		}
		
		return result;
	}
}
