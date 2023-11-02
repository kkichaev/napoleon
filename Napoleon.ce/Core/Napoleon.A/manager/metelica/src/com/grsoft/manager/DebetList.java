package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.manager.documents.MDebtDocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class DebetList extends Activity {
	private static final String ORG_ID = "orgid";
	private static final String USER_ID = "userid";
	private ExpandableListView list;
	private TextView tvSum;
	private TextView tvOrg;
	
 	public static void open(Context context, String id, String userid){
		Intent intent = new Intent(context, DebetList.class);
		intent.putExtra(ORG_ID, id);
		intent.putExtra(USER_ID, userid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debetlist);
		inflateView();
		init();
	}

	private void init() {
		String id = getIntent().getStringExtra(ORG_ID);
		DebetListAdapter adapter = new DebetListAdapter(this, id);
		list.setAdapter(adapter);
		list.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Document<?> d = (Document<?>) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				d.open(v.getContext());
				return true;
			}
		});
		
		tvSum.setText(Util.IntToScaleStr(adapter.sum, Consts.SUM_SCALE));
	
		MOrgImpl org = new MOrgImpl();
		org.read("id", id);
		
		tvOrg.setText(org.getData().name);
	}

	private void inflateView() {
		list = (ExpandableListView) findViewById(R.id.list);
		tvSum = (TextView) findViewById(R.id.tvSum);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
	}
}

class DLAData{
	public ManagerAgent agent = null;
	public MDebtDocList list = null;
	public int sum = 0;
}

class DebetListAdapter extends BaseExpandableListAdapter{
	private List<DLAData> data = new ArrayList<DLAData>();;
	private Context context;
	public int sum;
	
	public DebetListAdapter(Context context, String id){
		this.context = context;
		
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(){

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				MDebtDocList list =  new MDebtDocList("userid='" + item.data.id + "'", "date", true);
				
				if(list.getCount() > 0){
					DLAData d = new DLAData();
					d.agent = item.data;
					d.list = list;
					
					data.add(d);
					
					for( int i=0; i<list.getCount(); i++ ) {
						Document<?> c = list.get(i);
						d.sum += c.sum();
					}
					
					sum += d.sum;
					list.close();
				}
				
				return true;
			}
			@Override public boolean isDataNewInstance() { return true; }
			},null);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).list.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) { return 0; }

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.debetlistrow, null);
	
		Document<?> d = (Document<?>) getChild(groupPosition, childPosition);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvOther);
		tv.setText(d.getDescription(context));
		tv = (TextView) convertView.findViewById(R.id.tvDate);
		tv.setText(Util.simpleDateFormat.format(d.getDate()));
		tv = (TextView) convertView.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE));
			
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) { return data.get(groupPosition).list.getCount(); }

	@Override
	public Object getGroup(int groupPosition) { return data.get(groupPosition); }

	@Override
	public int getGroupCount() { return data.size(); }

	@Override
	public long getGroupId(int groupPosition) {	return 0; }

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.debetlistgroup, null);
		
		DLAData group = (DLAData) getGroup(groupPosition);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(group.agent.name);
		
		tv = (TextView) convertView.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(group.sum, Consts.SUM_SCALE));
		
		convertView.setBackgroundResource(R.drawable.list_grey_selector );
		
		return convertView;
	}

	@Override public boolean hasStableIds() {	return true; }

	@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
	
}
