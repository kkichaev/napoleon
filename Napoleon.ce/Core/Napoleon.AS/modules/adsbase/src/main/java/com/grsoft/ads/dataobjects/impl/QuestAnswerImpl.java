package com.grsoft.ads.dataobjects.impl;

import android.content.Context;

import com.grsoft.ads.QuestionWebView;
import com.grsoft.ads.dataobjects.QuestAnswer;
import com.grsoft.napoleon.documents.CreatableDocument;

public class QuestAnswerImpl extends CreatableDocument<QuestAnswer> {
	
	@Override
	public void open(Context context) {
		QuestionImpl quest = new QuestionImpl();
		quest.getData().idquest = data.question;
		boolean res = quest.read();
		quest.close();
		
		if(res)
			QuestionWebView.open(context, quest.getRowid(), data.id, getRowid());
		
	}

}
