package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import android.content.Context;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Refregerator;
import com.grsoft.dataobjects.RfrItem;
import com.grsoft.dataobjects.RfrOut;
import com.grsoft.napoleon.RfrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.RfrDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

public class RfrOutImpl extends CreatableDocument<RfrOut> {

	@Override
	public void open(Context context) {
		RfrEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = orgId;
		oi.read();
		oi.close();
		
		data.items = new ArrayList<RfrItem>();
		if( o.refrigerators != null )
			for(Refregerator i : o.refrigerators) {
				RfrItem item = new RfrItem();
				item.id = i.id;
				item.name = i.name;
				data.items.add(item);
			}
		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public long write() {
		long ret = super.write();
		if( ret != ExtrasConst.INVALID_ID )
			RfrDoc.instance().refreshDocSum(data.id);
		return ret;
	}
}
