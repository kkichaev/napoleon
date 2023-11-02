package com.grsoft.napoleon;

import java.io.File;

import com.grsoft.database.ReportAnswerHitching;
import com.grsoft.dataobjects.ReportAnswer;
import com.grsoft.dataobjects.impl.ReportAnswerImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ReportView extends BaseActivity {
	public static Class<? extends Activity> activity = ReportView.class;
	
	public static void open(Context context, String id) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.ORG_ID_STR, id);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_view);
		
		String id = getIntent().getExtras().getString(ExtrasConst.ORG_ID_STR);

		// empty string is valid id
		if (id != null /*&& id.length() > 0*/){
			ReportAnswerImpl ra = new ReportAnswerImpl();
			ReportAnswer r = ra.getData();
			r.id = id;
			
			if (ra.read()){
				WebView wv = (WebView) findViewById(R.id.webView);
				WebSettings settings = wv.getSettings();
				settings.setSupportZoom(true);
				
				String fileName = ReportAnswerHitching.REPORT_DIRECTORY + id + ReportAnswerHitching.REPORT_EXTENTION;
				File file = new File(fileName);
				if(((CfgNplW)ConfigManager.getConfig()).saveReportsToCard && file.isFile()) {
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
				settings.setBuiltInZoomControls(true);
				settings.setUseWideViewPort(true);
				settings.setLoadWithOverviewMode(true);
			}
			
			ra.close();
		}
	}
}
