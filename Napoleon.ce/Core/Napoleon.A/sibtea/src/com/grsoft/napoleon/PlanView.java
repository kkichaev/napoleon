package com.grsoft.napoleon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PlanReport;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.view.BaseActivity;

public class PlanView extends BaseActivity {
	public static void open(Context context) {
		
		boolean opened = false;
		
		PlanReport plan = new PlanReport();
		String table = DataObjectInfo.getInstance().getTableName(plan.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(plan, table, null);
		r.close();
		
		if( bdo ) {
			File file = new File(Environment.getExternalStorageDirectory(), Path.SHARED_FOLDER + "/report.html");
			try {
			    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				out.write(plan.report);
				out.close();
				
				opened = true;
				
				Intent in = new Intent(Intent.ACTION_VIEW);
	            in.setDataAndType(Uri.fromFile(file), "text/html");
	            context.startActivity(in);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(!opened ) {
			Intent i = new Intent(context, PlanView.class);
			context.startActivity(i);
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_view);
	
		PlanReport plan = new PlanReport();
		String table = DataObjectInfo.getInstance().getTableName(plan.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(plan, table, null);
		r.close();
		
		if( bdo ) {
			WebView wv = (WebView) findViewById(R.id.webView1);

			String html;
			try {
				html = new String(plan.report, "windows-1251");
				wv.loadDataWithBaseURL(null, html, "text/html", "windows-1251", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}		
}
