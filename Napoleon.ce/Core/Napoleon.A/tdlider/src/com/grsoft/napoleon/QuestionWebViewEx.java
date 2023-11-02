package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemEx;

public class QuestionWebViewEx extends QuestionWebView {
	protected void fillNecessaryQuestion(HashMap<String, String> ids) {
		for (QuestionItem qi : questionImpl.getData().items) {
			QuestionItemEx qie = (QuestionItemEx) qi;

			if (qie.optional == 0)
				ids.put(qi.iditem, qi.id);
		}
	}
}
