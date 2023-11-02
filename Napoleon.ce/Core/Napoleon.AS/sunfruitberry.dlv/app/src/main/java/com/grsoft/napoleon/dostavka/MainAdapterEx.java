package com.grsoft.napoleon.dostavka;

import java.util.Date;

import com.grsoft.dataobjects.DriverRouteActions;
import com.grsoft.dataobjects.RouteItemEx;
import com.grsoft.dataobjects.RouteItemRow;
import com.grsoft.dataobjects.RoutePoint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MainAdapterEx extends MainAdapter {

	String activeItem;
	
	public MainAdapterEx(Context context, Date workDate, OnClickListener mapClick, boolean filterActive) {
		super(context, workDate, mapClick, filterActive);
	}

	@Override
	protected String getName(RoutePoint org, RouteItemRow item) {
		RouteItemEx i = (RouteItemEx) item.item;
		String name = org.name + " с " + i.from + " по " + i.till;
		return name;
	}
	
	@Override
	protected void load(Date workDate, boolean active) {
		activeItem = DriverRouteActions.getActiveItemId();
		super.load(workDate, active);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View v = super.getView(position, view, parent);
		
		RouteItemRow row = (RouteItemRow) getItem(position);
		if(row.item.itemid.equals(activeItem) && !row.isFinished) {
			v.setBackgroundColor(0xFFEBBFB0);
		}
		return v;
	}
	
	@Override
	protected int getRouteIndexColor(RouteItemRow row) {
		if(row.item.itemid.equals(activeItem)) {
			return 0xFFEBBFB0;
//			return context.getResources().getColor(R.color.active_item);
		}
		return super.getRouteIndexColor(row);
	}
}
