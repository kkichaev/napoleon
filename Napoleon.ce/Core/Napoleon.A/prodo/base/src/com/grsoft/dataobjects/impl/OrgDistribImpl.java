package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DistribMatrixItem;
import com.grsoft.dataobjects.OrgDistrib;
import com.grsoft.napoleon.OrgDistribDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.OrgDistribDoc;
import com.grsoft.util.GpsCoord;

public class OrgDistribImpl extends CreatableDocument<OrgDistrib> {

	@Override
	public void open(Context context) {
		OrgDistribDetail.open(context, this);
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		boolean ret = super.init(context, orgId, gpsCoord);
		data.date = data.created;
		write();
		return ret;
	}
	
	@Override
	public long write() {
		long ret = super.write();
		OrgDistribDoc.instance().refreshDocSum(data.id);
		return ret;
	}

	public void changeItem(String id) {
		if( !isEditable() )
			return;
		
		for(DistribMatrixItem i : data.items)
			if(i.id.equals(id)) {
				data.items.remove(i);
				write();
				return;
			}
		
		DistribMatrixItem oai = new DistribMatrixItem();
		oai.id = id;
		data.items.add(oai);
		write();
	}

}
