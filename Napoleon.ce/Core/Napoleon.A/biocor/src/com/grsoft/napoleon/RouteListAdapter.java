package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgFoldersImplEx;
import com.grsoft.network.BaseSimpleAdapter;
import com.grsoft.util.Util;


public class RouteListAdapter extends BaseSimpleAdapter{
	public List<OrgFolders> data  = new ArrayList<OrgFolders>();
	public Context ctx;
	
	public RouteListAdapter(Context ctx) {
		this.ctx = ctx;
		
		Date date = Util.resetTime(new Date());
		String where = String.format("date >= %d", date.getTime());
		DataTraveler.travel(DbObject.getDataType(OrgFolders.class), new DataTraveler.Travel<OrgFolders>(){
			@Override public boolean isDataNewInstance() { return true; }
			
			@Override
			public boolean travel(DataTraveler<OrgFolders> item) {
				if(item.data.items.size() > 0)
					data.add(item.data);
				return true;
			}} , where);
		
		Collections.sort(data, new Comparator<OrgFolders>() {
			@Override public int compare(OrgFolders lhs, OrgFolders rhs) { return ((OrgFoldersEx)lhs).date.compareTo(((OrgFoldersEx)rhs).date); }});
	}
	
	@Override
	public int getCount() {	return data.size(); }

	@Override
	public Object getItem(int position) { return data.get(position);}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(ctx, R.layout.routelistrow, null);
		
		OrgFolders of = (OrgFolders) getItem(position);
		TextView tv = (TextView) view.findViewById(R.id.tvDate);
		tv.setText(of.name);
		
		tv = (TextView) view.findViewById(R.id.tvCount);
		tv.setText(Integer.toString(of.items.size()));
		
		return super.getView(position, view, parent);
	}

	public void removeItem(int pos) {
		OrgFolders o = (OrgFolders) getItem(pos);
		OrgFoldersImpl impl = new OrgFoldersImplEx();
		impl.read("name", o.name);
		impl.delete();
		impl.close();
		
		data.remove(o);
		notifyDataSetChanged();
	}
}
