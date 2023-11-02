package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;

import com.grsoft.dataobjects.ReportAnswerSPK;
import com.grsoft.dataobjects.impl.ReportAnswerSPKImpl;
import com.grsoft.view.BaseActivity;

public class ReportWebView extends BaseActivity {
	private final static String NAME = "name";
	
	public static void open(Context context, String name){
		Intent intent = new Intent(context, ReportWebView.class);
		intent.putExtra(NAME, name);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_web_view);
		
		String name = getIntent().getExtras().getString(NAME);
		
		if (name != null && name.length() > 0){
			ReportAnswerSPKImpl ra = new ReportAnswerSPKImpl();
			ra.getData().name = name;
			
			if (ra.read()){
				WebView wv = (WebView) findViewById(R.id.webView1);
				WebSettings settings = wv.getSettings();
				settings.setSupportZoom(true);

				ReportAnswerSPK r = ra.getData();
				String html;
				try {
					html = new String(r.report, r.encoding);
					wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				settings.setBuiltInZoomControls(true);
				settings.setUseWideViewPort(true);
				settings.setLoadWithOverviewMode(true);
			}
			
			ra.close();
		}
	}
}
