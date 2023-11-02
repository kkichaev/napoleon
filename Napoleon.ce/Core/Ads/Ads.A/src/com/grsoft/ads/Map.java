package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.grsoft.ads.dataobjects.Client;
import com.grsoft.ads.dataobjects.impl.ClientImpl;

public class Map extends MapActivity {
	private static final String CLIENT_ID = "client_id";
	private static final String TAG = "Map";
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public static void open(Activity activity, String clientid){
		Intent intent = new Intent(activity, Map.class);
		intent.putExtra(CLIENT_ID, clientid);
		activity.startActivity(intent);
	}
	
	public static String adjustAddress(String address){
		return address.substring(address.indexOf(",") + 1, address.length()); 
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);
		MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapView.setStreetView(true); 
		
	    ClientImpl clientImpl = new ClientImpl();
		Client client = clientImpl.getData(); 
		client.id = getIntent().getStringExtra(CLIENT_ID);
		boolean readResult = clientImpl.read();
		clientImpl.close();
		
		if (readResult){
			String address = clientImpl.getData().address;
			String name = clientImpl.getData().name;
			Geocoder geocoder = new Geocoder(this);
			address = adjustAddress(address);
			Log.d(TAG, "address: " + address);
			try{
	        	List<Address> la = geocoder.getFromLocationName(address, 1);
	        	
	        	if(la.size() > 0){
		        	List<Overlay> mapOverlays = mapView.getOverlays();
			        Drawable drawable = this.getResources().getDrawable(R.drawable.client);
			        ClientItemizedOverlay itemizedoverlay = new ClientItemizedOverlay(drawable, this);
			        GeoPoint point = new GeoPoint((int)(la.get(0).getLatitude() * 1e6),
			        		(int)(la.get(0).getLongitude() *1e6));
			        OverlayItem overlayitem = new OverlayItem(point, address, name);
			        itemizedoverlay.addOverlay(overlayitem);
			        mapOverlays.add(itemizedoverlay); 
			        
			        mapView.getController().setCenter(point);
			        mapView.getController().setZoom(15);
	        	}
	        } catch(Exception e){
	        	e.printStackTrace();
	        } 
		}
	}
}

class ClientItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private Context context;
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
	
	public ClientItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = overlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}
}
