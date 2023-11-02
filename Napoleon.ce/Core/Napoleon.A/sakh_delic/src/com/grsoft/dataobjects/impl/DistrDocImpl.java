package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DistrDoc;
import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.DistrPrice;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.DistrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class DistrDocImpl extends CreatableDocument<DistrDoc> {

	@Override
	public void open(Context context) {
		DistrEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = orgId;
		oi.read();
		
		data.name = o.name;
		
		data.items = new ArrayList<DistrItem>();
		
		DistrPrice dp = new DistrPrice();
		String table = DataObjectInfo.getInstance().getTableName(DistrPrice.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(dp, table, null, "number");
		while(bdo) {
			DistrItem item = new DistrItem(dp);
			data.items.add(item);
			
			bdo = r.selectNext(dp);
		}
		r.close();
		write();
		return true;
	}
}
