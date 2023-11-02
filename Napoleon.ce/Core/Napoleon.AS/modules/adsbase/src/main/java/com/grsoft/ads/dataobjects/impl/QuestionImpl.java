package com.grsoft.ads.dataobjects.impl;

import android.app.Activity;
import android.content.Context;
import com.grsoft.ads.QuestionWebView;
import com.grsoft.ads.dataobjects.Question;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class QuestionImpl extends Document<Question> {

	@Override
	public void open(Context context) {
		String orgid = ((Activity)context).getIntent()
			.getStringExtra(ExtrasConst.ORG_ID_STR);
		QuestionWebView.open(context, getRowid(), orgid);
	}
	
	@Override
	public String getDescription(Context context) {
		return getData().name;
	}
}
