package com.grsoft.napoleon;

import com.grsoft.dataobjects.RoutePhotoItem;
import com.grsoft.dataobjects.RoutePhotos;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.view.BaseActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class RoutePhotoList extends BaseActivity {
	
	static String STR_TAG = "str_Tag";

	String content;
	
	@SuppressLint("DefaultLocale")
	public static void open(Context context, RoutePhotos photos) {
		Config cfg = ConfigManager.getConfig();
		
		StringBuilder sb = new StringBuilder();
        sb.append("<html><head>\n<meta charset='utf-8'>\n<style type='text/css'>\ndiv.inline{\n    display:inline-block;\n   margin-right: 6px}" +
                "p.nomargine{\n    margin-top: 0px;\n    text-align: center;\n}\n</style>\n</head>\n<body>\n");
        
        
        String href = "http://" + cfg.address + ":" + Integer.toString(cfg.port) + "/";
        int idx = 1;
        for(RoutePhotoItem i : photos.items) {
        	String[] hw = i.smallSize.split("\\*");
        	
        	String name = String.format("%d", idx++);
        	
            sb.append("<div class='inline' style='width: " + hw[0] + "px;'>\n");
            String smallImg = i.smallName.replace("\\", "/");
            if (smallImg.startsWith("/"))
               smallImg = smallImg.substring(1);

            String img = i.name.replace("\\", "/");
            if (img.startsWith("/"))
               img = img.substring(1);
            sb.append("<a href='").append(href + img).append("'>\n");
            sb.append("<img src='" + href + smallImg + "' width='" + hw[0] + "px' height='" + (hw.length > 1 ? hw[1] : "165") + "px' />\n</a>");
            sb.append("<p class='nomargine'>" + name + "</div>");
            if((idx % 3) == 0)
            	sb.append("<div/>");
        }
        
        sb.append("</body></html>");
        
        Intent i = new Intent(context, RoutePhotoList.class);
        i.putExtra(STR_TAG, sb.toString());
        context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_photo_list);
		
		WebView wv = (WebView) findViewById(R.id.webView);
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		content = b.getString(STR_TAG);
		
		WebSettings settings = wv.getSettings();
		settings.setSupportZoom(true);

		wv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
		
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(STR_TAG, content);
	}
}
