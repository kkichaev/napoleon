package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DoverImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class DoverView extends Activity{
public ExpandableListView list;
	
	public static void open(Context context){
		Intent i = new Intent(context, DoverView.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remake);
		
		list = (ExpandableListView) findViewById(R.id.list);
		list.setAdapter(new DoverAdapter(this));
		list.setDividerHeight(0);
	}
}

class DoverAdapter implements ExpandableListAdapter{
	private Context context;
	private List<Org> group = new ArrayList<Org>();
	private Map<String, List<DoverImpl>> childs= new HashMap<String, List<DoverImpl>>();
	
	public DoverAdapter(Context context){
		this.context = context;
		loadData();
		
		for(List<DoverImpl> list: childs.values())
			Collections.sort(list, comparer);
		
		Collections.sort(group, new Comparator<Org>() { @Override public int compare(Org lhs, Org rhs) { return lhs.name.compareTo(rhs.name); }});
	}

	protected void loadData() {
		List<Long> ri = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Dover.class), null, null);
		
		for(long r : ri){
			DoverImpl d = new DoverImpl();
			
			if (d.read(r)){
				OrgImpl org = new OrgImpl();
				
				if(org.read("id", d.getId())){
					if (!childs.containsKey(d.getId())){
						childs.put(d.getId(), new ArrayList<DoverImpl>());
						group.add(org.getData());
					}
					
					childs.get(d.getId()).add((DoverImpl) d);
				}
			}
			
			d.close();
		}
	}
	
	private Comparator<DoverImpl> comparer = new Comparator<DoverImpl>() {
		@Override
		public int compare(DoverImpl lhs, DoverImpl rhs) {
			int result = lhs.getData().date.compareTo(rhs.getData().date);
			
			if (result == 0)
				result = lhs.getData().number.compareTo(rhs.getData().number);
			
			return result;
		}
	};

	@Override
	public int getGroupCount() { return group.size();}

	@Override
	public int getChildrenCount(int groupPosition) {
		String id = group.get(groupPosition).id;
		return childs.get(id).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String id = group.get(groupPosition).id;
		return childs.get(id).get(childPosition);
	}

	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.actdlvgroup, null);
		
		Org o = (Org) getGroup(groupPosition);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(o.name);
		
		tv = (TextView) convertView.findViewById(R.id.tvAddress);
		tv.setText(o.address);
				
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.doverchild, null);
		
		DoverImpl i = (DoverImpl) getChild(groupPosition, childPosition);
		Dover d = (Dover) i.getData();
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvNumber);
		tv.setText(d.number);
		
		tv = (TextView) convertView.findViewById(R.id.tvDate);
		tv.setText(com.grsoft.util.Util.simpleDateFormat.format(d.date));
		
		tv = (TextView) convertView.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE));
		
		convertView.setBackgroundResource(childPosition % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);		
		
		return convertView;
	}

	@Override public long getGroupId(int groupPosition) { return 0; }
	@Override public long getChildId(int groupPosition, int childPosition) { return 0;}
	@Override public boolean hasStableIds() { return false; }
	@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
	@Override public boolean areAllItemsEnabled() {	return true; }
	@Override public boolean isEmpty() { return false; }
	@Override public void onGroupExpanded(int groupPosition) { }
	@Override public void onGroupCollapsed(int groupPosition) { }
	@Override public long getCombinedChildId(long groupId, long childId) { return 0; }
	@Override public long getCombinedGroupId(long groupId) { return 0; }
	@Override public void registerDataSetObserver(DataSetObserver observer) {}
	@Override public void unregisterDataSetObserver(DataSetObserver observer) {}
}
