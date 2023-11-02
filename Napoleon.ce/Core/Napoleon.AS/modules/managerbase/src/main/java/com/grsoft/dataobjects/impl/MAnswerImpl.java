package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.MAnswer;
import com.grsoft.manager.QuestEdit;
import com.grsoft.manager.QuestionDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class MAnswerImpl extends CreatableDocument<MAnswer> {

	@Override
	public void open(Context context) { QuestEdit.open(context, this); }

}
