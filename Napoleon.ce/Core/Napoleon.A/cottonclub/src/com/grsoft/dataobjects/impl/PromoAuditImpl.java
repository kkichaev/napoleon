package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.AnswerId;
import com.grsoft.dataobjects.PromoAudit;
import com.grsoft.napoleon.PromoAuditEdit;
import com.grsoft.napoleon.documents.PromoAuditDoc;

public class PromoAuditImpl extends Answerable<PromoAudit> {

	@Override
	public void open(Context context) {
		PromoAuditEdit.open(context, this);
	}

	@Override
	public void add(AnswerEx answer) {
		AnswerId answerid = new AnswerId();
		answerid.answerid = answer.answerid;
		data.answer.add(answerid);
	}
	
	@Override
	protected String getObjectName() {
		return PromoAuditDoc.instance().getObjectName();
	}
}
