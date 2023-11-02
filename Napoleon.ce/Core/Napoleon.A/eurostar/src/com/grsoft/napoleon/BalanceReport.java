package com.grsoft.napoleon;

import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.impl.ReportImpl;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


public class BalanceReport extends Activity {
	private WebView wv;
	
	public static void open(Context context){
		Intent intent = new Intent(context, BalanceReport.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balance);
		
		wv = (WebView) findViewById(R.id.webView);
		
		final String REPORT_NAME = "cash_report"; 
		
		ReportImpl report = new ReportImpl();
		if(report.read("id", REPORT_NAME)){
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
