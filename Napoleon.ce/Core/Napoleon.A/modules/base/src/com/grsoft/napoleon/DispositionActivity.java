package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgLocationImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Coordutils;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DispositionActivity extends Activity implements LocationListener {
	public static Class<? extends Activity> activity = DispositionActivity.class;
	private final static String ORGID = "ORGID";
	private WebView webView;
	
	private static final double DEFAULT_MY_RADIUS = 50;
	private OrgLocationImpl orgLocation = new OrgLocationImpl();
	
	private static class MapData
	{
		public String id = "";
		public int idx = 0;
		public String name = "";
		public String address = "";
		public double longitude = 0.0;
		public double latitude = 0.0;
		public String color = "";
		public double radius = 0.0;
	}
	
	private static class MapObject{
		public List<MapData> map = new ArrayList<DispositionActivity.MapData>();
		public Location mypos = null;
		public double radius = 0.0;
	}
	
	public static void open(Activity context, String orgid) {
		Intent i = new Intent(context, activity);
		i.putExtra(ORGID, orgid);
		context.startActivityForResult(i, R.id.disposition_result);
	}
	
	public DbObject<?> initOrg(){
		return new OrgImpl();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.disposition);
		
		webView = (WebView) findViewById(R.id.web);
		
		String id = getIntent().getStringExtra(ORGID);
		DbObject<?> orgIml = initOrg(); 
		MapObject data = new MapObject();
		
		if (orgLocation.read("id", id) && orgIml.read("id", id)) {
			MapData mp = new MapData();
			
			Org org = (Org) orgIml.getData();
			mp.id = org.id;
			mp.name = org.name;
			mp.address = org.address;
			mp.latitude = ((double)orgLocation.getData().latitude) / Consts.GPS_SCALE;
			mp.longitude = ((double)orgLocation.getData().longitude) / Consts.GPS_SCALE;;
			mp.idx = 1;
			mp.color = "red";
			mp.radius = ConfigHelper.getOrgRaduis();
			
			data.map.add(mp);
		}
	
		Location m = GPSUtilNew.getCurrentLocation(this);
		data.mypos = m;
		data.radius  = getMyRadius(m);
		
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
				
				try {
					dismissDialog(R.id.wait_dlg);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				
				try {
					showDialog(R.id.wait_dlg);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				try {
					dismissDialog(R.id.wait_dlg);
				}catch(Exception e) {
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
	
	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Config cfg = ConfigManager.getConfig();
		LocationManager m = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		try {
			m.requestLocationUpdates(LocationManager.GPS_PROVIDER,  cfg.gpsFrequience, cfg.gpsDistance, this);
			m.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, cfg.gpsFrequience, cfg.gpsDistance, this);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		LocationManager m = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		m.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!isImIntoOrg(orgLocation.getData(), location)){
			Gson gson = new Gson();
			String ds = gson.toJson(location);
			webView.loadUrl("javascript:setMyLocation("+ds+");");
		}else {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	public static boolean isCircleIntersects(double x1, double y1, double r1, double x2, double y2, double r2) {
		double d = Coordutils.distance(x1,y1,x2,y2);
		return (r1 + r2) >= d;
	}
	
	public static boolean isImIntoOrg(OrgLocation orgLocation, Location myLocation) {
		double x2 = (double)orgLocation.longitude / Consts.GPS_SCALE;
		double y2 = (double)orgLocation.latitude / Consts.GPS_SCALE;
		
		return isCircleIntersects(myLocation.getLongitude(), myLocation.getLatitude(), 
				getMyRadius(myLocation), x2, y2, ConfigHelper.getOrgRaduis());
	}
	
	public static boolean isNeedDisposition(Context context, String orgId, String docType) {
		Location cur = GPSUtilNew.getCurrentLocation(context);
		OrgLocationImpl loc = new OrgLocationImpl();
		return ConfigHelper.isDisposition(docType) &&
				loc.read("id", orgId) && 
				(cur == null || !isImIntoOrg(loc.getData(), cur)); 
	}
	
	private static double getMyRadius(Location im) {
		
		return im == null || im.getAccuracy() < DEFAULT_MY_RADIUS ? DEFAULT_MY_RADIUS : im.getAccuracy();
	}
}
