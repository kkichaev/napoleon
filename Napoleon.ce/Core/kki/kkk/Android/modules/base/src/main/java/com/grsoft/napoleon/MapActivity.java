package com.grsoft.napoleon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.MapLocation;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgLocationImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapActivity extends Activity {
	private WebView webView;
	public static String ITEMS_LIST = "items_list";
	public static void open(Context context, Serializable items) {
		Intent i = new Intent(context, MapActivity.class);
		i.putExtra(ITEMS_LIST, items);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.maps);
		webView = (WebView) findViewById(R.id.web);
		
		List<String> ids = (List<String>) getIntent().getSerializableExtra(ITEMS_LIST);
		
		OrgLocationImpl loc = new OrgLocationImpl();
		OrgImpl org = new OrgImpl(); 
		
		MapObject data = new MapObject();
		Set<String> pointDocs = getPoints();
		
		int pos = 1;
		
		for(String i : ids) {
			if (loc.read("id", i) && org.read("id", i)) {
				MapData mp = new MapData();
				mp.id = org.getData().id.replace('\t', ' ');
				mp.name = org.getData().name;
				mp.address = org.getData().address;
				mp.latitude = ((double)loc.getData().latitude) / Consts.GPS_SCALE;
				mp.longitude = ((double)loc.getData().longitude) / Consts.GPS_SCALE;;
				mp.idx = pos++;
				mp.color = pointDocs.contains(i) ? "red" : "green";
				
				data.map.add(mp);
			}
		}

		Location mp = GPSUtilNew.getCurrentLocation(this);

		if(mp != null)
			data.mypos = new MapLocation(mp);

		MapHelper mh = new MapHelper();
		String html = mh.createMap(this, data, "openstreet");
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.addJavascriptInterface(this, "Android");
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);

				try{
					dismissDialog(R.id.wait_dlg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				
				showDialog(R.id.wait_dlg);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				try{
					dismissDialog(R.id.wait_dlg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return createWaitDlg();
		return super.onCreateDialog(id);
	}
	
	private Set<String> getPoints() {
		HashSet<String> result = new HashSet<String>();
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date start = c.getTime();
		c.add(Calendar.DATE, 1);
		Date finish = c.getTime(); 
		
		DatePeriod dp = new DatePeriod(start, finish);
		
		for(DocTypeBase dt : DocType.docTypes) {
			if (dt.isCreatable()) {
				for(Document<?> d : dt.docList(null, null, dp)) {
					result.add(d.getId());
				}
			}
		}
		
		return result;
	}

	public void handle(String id){
		OrgImpl org = new OrgImpl();
		if(org.read("id", id)) {
			DocumentsW.open(this, org.getData());
			finish();
		}
	}
	
	@SuppressWarnings("unused")
	private static class MapData
	{
		public String id = "";
		public int idx = 0;
		public String name = "";
		public String address = "";
		public double longitude = 0.0;
		public double latitude = 0.0;
		public String color = "";
	}
	
	private static class MapObject{
		public List<MapData> map = new ArrayList<MapActivity.MapData>();
		public MapLocation mypos = null;
	}
	
	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
}
