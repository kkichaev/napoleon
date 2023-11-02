package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.DriverRouteActions;
import com.grsoft.dataobjects.RoutePointEx;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class RoutePointViewEx extends RoutePointView {
	@Override
	protected boolean allowWork() {
		String active = DriverRouteActions.getActiveItemId();
		if(!active.equals(rii.getData().itemid))
			return false;
		
		return super.allowWork();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { makeVisit(); }
		});
		
		TextView tv = (TextView)findViewById(R.id.tvRemark);
		tv.setText(((RoutePointEx)rpi.getData()).deliveryNotes);
		tv.setVisibility(View.VISIBLE);
		
//		RoutePoint rp = rpi.getData();
		
//		tv = (TextView)findViewById(R.id.tvCName);
//		
//		if(rp.contacts.size() > 0 ){
//			tv.setText(rp.contacts.get(0).name+": ");
//			tv.setTag(rp.contacts.get(0));
//			tv.setOnClickListener(pressToPhoneCall);
//		}
	}

	protected int getLayoutId() { return R.layout.point_ex; }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itVisit);
		mi.setVisible(false);
		return true;
	}
}
