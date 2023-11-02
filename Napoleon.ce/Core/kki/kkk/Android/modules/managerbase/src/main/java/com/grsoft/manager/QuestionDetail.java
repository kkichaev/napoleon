package com.grsoft.manager;

import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.impl.MAnswerImpl;
import com.grsoft.dataobjects.impl.MQuestionImpl;
import com.grsoft.manager.R;
import com.grsoft.util.AnswerHTML;
import com.grsoft.util.ExtrasConst;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.webkit.WebView;

public class QuestionDetail extends FragmentActivity {
	public static Class<? extends QuestionDetail> activity = QuestionDetail.class;
	
	public static void open(Context context, MAnswerImpl answer) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.ROW_ID_FIELD, answer.getRowid());
		context.startActivity(i);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.m_question);
		
		WebView webView;
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true); 
        webView.getSettings().setSupportZoom(true);
	
        long rid = getIntent().getLongExtra(ExtrasConst.ROW_ID_FIELD, ExtrasConst.INVALID_ROWID);
        
        MQuestionImpl quest = new MQuestionImpl();
        Question q = quest.getData();
        
        MAnswerImpl answ = new MAnswerImpl();
        Answer a = answ.getData();
        
        answ.read(rid);
        answ.close();
        
        q.idquest = a.question;
        quest.read();
        quest.close();
        
        String html = AnswerHTML.makeHTML(q, a, false, this);
        
        webView.loadDataWithBaseURL(null, html, "text/html", null, null);
        webView.getSettings().setBuiltInZoomControls(true);
	}
}
