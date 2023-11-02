package com.grsoft.manager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class QuestionDetailNew extends QuestionDetail {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		View v = getLayoutInflater().inflate(R.layout.action_bar, null);
		TextView tv = (TextView) v.findViewById(R.id.tvTitle);
		tv.setText(getString(R.string.quest_doc_title));
		
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
	}
}
