package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.impl.ReportImpl;
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
		WebView wv = (WebView) findViewById(R.id.webView1);
		String id = getIntent().getExtras().getString(NAME); 
		
		ReportImpl report = new ReportImpl();
		if(report.read("id", id)){
			Report r = report.getData();
			try{
				String html = new String(r.report, r.encoding);
				wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
}
