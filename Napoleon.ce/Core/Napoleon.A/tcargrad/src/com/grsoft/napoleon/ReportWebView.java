package com.grsoft.napoleon;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.grsoft.database.GetReportsHitching;
import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.impl.ReportImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
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
			ReportImpl ra = new ReportImpl();
			Report r = ra.getData();
			r.id = name;
			
			if (ra.read()){
				WebView wv = (WebView) findViewById(R.id.webView1);
				
				String fileName = GetReportsHitching.REPORT_DIRECTORY + name + GetReportsHitching.REPORT_EXTENTION;
				File file = new File(fileName);
				if( ((CfgNpl)ConfigManager.getConfig()).saveReportsToCard && file.isFile()) {
					wv.loadUrl("file://" + fileName);
				} else {
					String html;
					try {
						html = new String(r.report, r.encoding);
						wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			ra.close();
		}
	}
}
