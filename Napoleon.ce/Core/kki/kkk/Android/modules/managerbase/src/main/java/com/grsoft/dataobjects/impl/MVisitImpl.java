package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitPreview;
import com.grsoft.manager.VisitDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;


public class MVisitImpl extends CreatableDocument<VisitPreview> {

	@Override
	public void open(Context context) {	VisitDetail.open(context, this); }

}
