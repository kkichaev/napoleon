package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.MainEx;
import com.grsoft.napoleon.OrgHelper;
import com.grsoft.napoleon.QuestAnswer;
import com.grsoft.napoleon.QuestionWebView;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;

public class QuestionImplEx extends QuestionImpl {
	@Override
	public void open(Context context) {
		String orgid = ((Activity)context).getIntent()
			.getStringExtra(ExtrasConst.ORG_ID_STR);

		if(hasAnswers(orgid))
			QuestAnswer.open(context, rowid, orgid);
		else {
			if (!MainEx.hardMode || OrgHelper.isEnabled(orgid))
				QuestionWebView.open(context, getRowid(), orgid);
		}
	}
}
