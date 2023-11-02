package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DlvRpt;
import com.grsoft.view.BaseActivity;

public class ReportWebView extends BaseActivity {
	
	public static void open(Context context){
		Intent intent = new Intent(context, ReportWebView.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_web_view);

		DlvRpt data = new DlvRpt();
		DbReader reader = new DbReader();
		
		if (reader.select(data, data.getTableName(), null)){
			WebView wv = (WebView) findViewById(R.id.webView1);
			try{
				String html = new String(data.report, data.encode);
				wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		reader.close();
	}
}
