package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.Route;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.dataobjects.RouteItemRow;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.network.BaseSimpleAdapter;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainAdapter extends BaseSimpleAdapter{
	protected List<RouteItemRow> data = new ArrayList<RouteItemRow>();
	protected DispatchImpl dispatch = DispatchImpl.create();
	protected RoutePointImpl point = new RoutePointImpl();
	protected Context context;
	protected View.OnClickListener mapClick;
	
	public MainAdapter(Context context, Date workDate, View.OnClickListener mapClick, boolean filter){
		this.context = context;
		this.mapClick = mapClick;
		load(workDate, filter); 
	}
	
	int routeIndex;
	
	@SuppressLint("DefaultLocale")
	protected void load(Date workDate,final boolean active){
		data.clear();
		long now = Util.getDayStart(workDate).getTime();
		routeIndex = 0;
		String routeWhere = String.format("start < %d and finish > %d and hidden = 0", now + 24 * 3600 * 1000, now); 
		
		DataTraveler.travel(Route.class, new DataTraveler.Travel<Route>(){
			@Override public boolean travel(final DataTraveler<Route> route) {
				String where = String.format("route='%s'", route.data.id);
				routeIndex++;
				
				Class<? extends DataObject> rtc = DbObject.getDataType(RouteItem.class);
				try {
					DbReader r = new DbReader();
					RouteItem item = (RouteItem) rtc.newInstance();
					boolean bdo = r.select(item, item.getTableName(), where);
					while(bdo) {
						if (!active || !(dispatch.readFromId(item.itemid) && dispatch.isDocFinished(context) == DispatchItem.DOC_COMPLETE))
							data.add(new RouteItemRow(item, routeIndex));
						
						item = (RouteItem) rtc.newInstance();
						bdo = r.selectNext(item);
					}
					r.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}}, routeWhere, "start");
		
		
		Collections.sort(data, new Comparator<RouteItemRow>() { @Override public int compare(RouteItemRow lhs, RouteItemRow rhs) {
			int cmp = lhs.routeIndex - rhs.routeIndex;
			if(cmp == 0)
				cmp = (int)(lhs.item.pos - rhs.item.pos);
			return cmp; 
		}});
	}

	@Override public int getCount() { return data.size(); }

	@Override public Object getItem(int position) { return data.get(position);}

	protected String getName(RoutePoint org, RouteItemRow item) {
		return org.name;
	}
	
	protected int getRouteIndexColor(RouteItemRow row) {
		return context.getResources().getColor(((row.routeIndex % 2) != 0) ?  R.color.odd_route : R.color.even_route);
	}
	
	@Override 
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(context, R.layout.routerow, null);
		
		RouteItemRow pt = (RouteItemRow) getItem(position);
		
		String name = context.getString(R.string.point_not_loaded, pt.item.id);
		String address = "";
		
		if(point.read("id", pt.item.id)){
			name = getName(point.getData(), pt);
			address = point.getData().address;
		}
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(name);
		
		tv = (TextView) view.findViewById(R.id.tvAddress);
		tv.setText(address);
		
		tv = (TextView) view.findViewById(R.id.tvRemark);
		if(tv != null && pt.item.remark.length() > 0)
			tv.setText(pt.item.remark);
		
		View btnMap = view.findViewById(R.id.btnMap); 
		btnMap.setTag(pt);
		btnMap.setOnClickListener(mapClick);
		
		View v = view.findViewById(R.id.vRouteIndex);
		v.setBackgroundColor(getRouteIndexColor(pt));

		view.setBackgroundResource(R.drawable.normal_selector);
		pt.isFinished = false;

		if(dispatch.readFromId(pt.item.itemid)) {
			int state =  dispatch.isDocFinished(context);

			if (state == DispatchItem.DOC_COMPLETE) {
				view.setBackgroundResource(R.drawable.grey_selector);
				pt.isFinished = true;
			}
			else if (state == DispatchItem.DOC_INITED)
				view.setBackgroundResource(R.drawable.turquoise_selector);

		}

		return view;
	}

	public void reload(Date workDate, boolean filter){
		load(workDate, filter);
		notifyDataSetChanged();
	}
}
