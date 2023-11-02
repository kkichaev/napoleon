package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.AnswerId;
import com.grsoft.dataobjects.CommonAudit;
import com.grsoft.dataobjects.CommonAuditItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.CommonAuditEdit;
import com.grsoft.napoleon.documents.CommonAuditDoc;
import com.grsoft.util.GpsCoord;

public class CommonAuditImpl extends Answerable<CommonAudit>{

	@Override public void open(Context context) { CommonAuditEdit.open(context, this); }

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = orgId;
		oi.read();
		oi.close();
		
		for(MatrixItem mi : o.price) {
			CommonAuditItem cai = new CommonAuditItem();
			cai.id = mi.id;
			data.items.add(cai);
		}
		
		return super.init(context, orgId, gpsCoord);
	}

	@Override
	public void add(AnswerEx answer) {
		AnswerId answerid = new AnswerId();
		answerid.answerid = answer.answerid;
		data.answer.add(answerid);
	}
	
	@Override
	protected String getObjectName() {
		return CommonAuditDoc.instance().getObjectName();
	}
}
