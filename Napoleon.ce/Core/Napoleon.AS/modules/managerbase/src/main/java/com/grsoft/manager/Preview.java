package com.grsoft.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class Preview extends Activity {
	public static Class<? extends Activity> activity = Preview.class;
	
	private static final String PATH = "path";
	
	public static void open(Context context, String path){
		Intent intent = new Intent(context, activity);
		intent.putExtra(PATH, path);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView wb = new WebView(this);
		wb.clearCache(true);
		
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html>")
		    .append("<html>")
			.append("<body>")
			.append("<div style='width:100%; text-align:center'>") 
		    .append("<img src='" + "file://" + getIntent().getStringExtra(PATH) + "' alt='' />")
		    .append("</div>")
		    .append("</body>")
		    .append("</html>");
		
		wb.getSettings().setBuiltInZoomControls(true);
		wb.getSettings().setSupportZoom(true);
		wb.loadDataWithBaseURL(null, html.toString(), "text/html", null, null);
		
		setContentView(wb);
	}
}
