package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Answer;
import com.grsoft.napoleon.QuestionWebView;
import com.grsoft.napoleon.documents.CreatableDocument;

public class AnswerImpl extends CreatableDocument<Answer> {
	
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
