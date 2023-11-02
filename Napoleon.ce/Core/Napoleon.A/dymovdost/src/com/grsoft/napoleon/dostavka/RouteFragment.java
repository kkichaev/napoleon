package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.DispathImpl;
import com.grsoft.network.BaseSimpleAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class RouteFragment extends Fragment {
	private ListView list;
	private BaseAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.route, container, false);
		list = (ListView) view.findViewById(R.id.list);
		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Main m = ((Main)getActivity());
				RoutePoint rp = (RoutePoint) parent.getItemAtPosition(position);
				m.saveActivePoint(rp.id);
				m.openPointFragment(rp.id);
			}});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(refresh, new IntentFilter(MainService.SYNC_FINISHED));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(refresh);
	}
	
	BroadcastReceiver refresh = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getBooleanExtra(MainService.SYNC_RESULT, false))
				reload();
		}
	};

	protected void reload() {((Adapter)adapter).reload();}
	
	OnClickListener mapClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RoutePoint rp = (RoutePoint) v.getTag();
			if(rp != null){
				String address = rp.address;
				String s = String.format("geo:0,0?q=%s", address );
				
				if(rp.latitude != 0 && rp.longitude != 0)
					s = String.format("geo:%s,%s", Util.IntToScaleStr(rp.latitude, Consts.GPS_SCALE), Util.IntToScaleStr(rp.longitude, Consts.GPS_SCALE));
				
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
					startActivity(intent);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
	
	private class Adapter extends BaseSimpleAdapter{
		List<RoutePoint> data = new ArrayList<RoutePoint>();
		DispathImpl d = new DispathImpl();
		
		public Adapter(){ load(); }
		
		private void load(){
			data.clear();
			
			DataTraveler.travel(RoutePoint.class, new DataTraveler.Travel<RoutePoint>(){

				@Override
				public boolean travel(DataTraveler<RoutePoint> item) {
					data.add(item.data);
					return true;
				}
				@Override public boolean isDataNewInstance() { return true; }}, null
			);
			
			Collections.sort(data, new Comparator<RoutePoint>() { @Override public int compare(RoutePoint lhs, RoutePoint rhs) { return lhs.pos - rhs.pos; }});
		}

		@Override public int getCount() { return data.size(); }

		@Override public Object getItem(int position) { return data.get(position);}

		@Override 
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(getActivity(), R.layout.routerow, null);
			
			RoutePoint pt = (RoutePoint) getItem(position);
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(pt.name);
			
			tv = (TextView) view.findViewById(R.id.tvAddress);
			tv.setText(pt.address);
			
			View btnMap = view.findViewById(R.id.btnMap); 
			btnMap.setTag(pt);
			btnMap.setOnClickListener(mapClick);
			
			if(d.readFromId(pt.id) && !d.isEditable())
				view.setBackgroundResource(R.drawable.grey_selector);
			else
				view.setBackgroundResource(R.drawable.normal_selector);
			
			return view;
		}

		public void reload(){
			load();
			notifyDataSetChanged();
		}
	}
}	
