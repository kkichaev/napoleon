package com.grsoft.manager;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.OrdDlv;
import com.grsoft.dataobjects.OrderRequest;
import com.grsoft.dataobjects.impl.OrderPendingImpl;
import com.grsoft.dataobjects.impl.OrderRequestImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class OrderReviewEdit extends FragmentActivity {
	public static final String REFRESH_ACTION = "com.grsoft.manager.OrderReviewEdit.REFRESH_ACTION";
	private View btnSync;
	private ExpandableListView list;
	private OrderReviewAdapter adapter;
	private View btnSend;
	private SyncOrdDlv syncProcess;
	private SendOrderRequest sendProcess;
	private LinearLayout headerView;
	
	public static void open(Context context){
		Intent intent = new Intent(context, OrderReviewEdit.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.orderreview);
		inflateView();
		init();
	}

	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.getDefault());
	
	private void init() {
		btnSync.setOnClickListener(syncClick());
		adapter = createAdapter();
		headerView = (LinearLayout) View.inflate(this, R.layout.orddlvheader, null);
		
		for(int id : new int[]{R.id.tvAgent, R.id.tvOrg, R.id.tvDate, R.id.tvSum, R.id.tvSumD}){
			View v = headerView.findViewById(id);
			
			if (v != null)
				v.setOnClickListener(sortClick);
		}
		
		list.addHeaderView(headerView);
		list.setAdapter(adapter);
		list.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				OrdDlv orddlv = (OrdDlv)  parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				
				if(orddlv != null){
					DecisionEdit.Params args = new DecisionEdit.Params();
					args.order = orddlv.created.getTime();
					args.userid = orddlv.userid;
					args.id = orddlv.id;
					
					DecisionEdit.showDialog(OrderReviewEdit.this, args);
				}
				
				return true;
			}
		});
		
		for(int i = 0; i < adapter.getGroupCount(); i++)
			list.expandGroup(i);
		
		btnSend.setOnClickListener(sendClick());
		
		syncProcess = new SyncOrdDlv(this, updctrl);
		sendProcess = new SendOrderRequest(this, updctrl);
		
		pressed(headerView.findViewById(R.id.tvAgent));
	}
	
	private void pressed(View view){
		select(view, Typeface.BOLD_ITALIC);
	}
	private void select(View view, int type) {
		if(view != null && view instanceof TextView){
			TextView tv = (TextView) view;
			tv.setTypeface(Typeface.create(tv.getTypeface(), type));
		}
	}
	
	OnClickListener sortClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			reset();
			
			Object f = view.getTag();
			
			if (f != null){
				adapter.sort(f.toString());
				adapter.notifyDataSetChanged();
				pressed(view);
			}
		}

		private void reset() {
			if(headerView != null){
				for (int i = 0; i < headerView.getChildCount(); i++)
				{
					View v = headerView.getChildAt(i);
					select(v, Typeface.NORMAL);
				}
			}
		}
	};

	private OnClickListener sendClick() {
		return new OnClickListener() {
			@Override public void onClick(View v) { sendProcess.execute(); } };
	}

	UpdateCtrl updctrl = new UpdateCtrl() {
		@Override public void updateCtrl(boolean enabled) {
			btnSync.setEnabled(enabled);
			btnSend.setEnabled(enabled);}
		@Override public void onFinish(boolean success) {	adapter.notifyDataSetChanged();	}					
	};
	
	private OrderReviewAdapter createAdapter() { return new OrderReviewAdapter(this); }

	private OnClickListener syncClick() { return new OnClickListener() { @Override public void onClick(View v) { syncProcess.start(); }	};
	}

	private void inflateView() {
		btnSync = findViewById(R.id.btnSync);
		list = (ExpandableListView) findViewById(R.id.list);
		btnSend = findViewById(R.id.btnSend);
	}
	
	BroadcastReceiver applyDecision = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			adapter.fillData();
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(applyDecision, new IntentFilter(REFRESH_ACTION));
		
		list.collapseGroup(1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(applyDecision);
	}
}

@SuppressLint("UseSparseArrays")
class OrderReviewAdapter extends BaseExpandableListAdapter{
	private Context context;
	private String[] group = new String[]{};
	private List<OrdDlv> waiting = new ArrayList<OrdDlv>();
	private List<OrdDlv> processed = new ArrayList<OrdDlv>();
	private Map<Long, OrderRequest> decisions = new HashMap<Long, OrderRequest>();
	private String sortField = "agent";
	
	private OnClickListener sumOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() != null){
				OrderPendingImpl ord = new OrderPendingImpl();
				if (ord.read("created", (Date)v.getTag()))
					ord.open(v.getContext());
			}
		}
	};
	
	private OnClickListener debtOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() != null){
				final OrdDlv od = (OrdDlv) v.getTag();
				SyncDelivery sync = new SyncDelivery(context, new UpdateCtrl() {
					@Override public void updateCtrl(boolean enabled) {}
					@Override public void onFinish(boolean success) { DebetList.open(context, od.id, od.userid); }
				});
				sync.start(od.id, od.userid);
			}
		}
	};
	
	public OrderReviewAdapter(Context context){
		this.context = context;
		group = context.getResources().getStringArray(R.array.orddlvstatus);
		fillData();
	}
	
	private int cmpfunc(Field f, Object lhs, Object rhs){
		try{
			if (f.getType() == String.class)
				return f.get(lhs).toString().compareTo(f.get(rhs).toString());
			else if (f.getType() == Date.class)
				return ((Date)f.get(lhs)).compareTo((Date)f.get(rhs));
			else if (f.getType() == int.class)
				return f.getInt(lhs) - f.getInt(rhs);
			else if (f.getType() == long.class)
				return (int)(f.getLong(lhs) - f.getLong(rhs)); 
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public void sort(String field) {
		try{
			sortField = field;
			final Field f = OrdDlv.class.getField(field);
			
			Comparator<OrdDlv> cmp = new Comparator<OrdDlv>() {
				@Override public int compare(OrdDlv lhs, OrdDlv rhs) { return cmpfunc(f, lhs, rhs); }
			};
			
			Collections.sort(waiting, cmp);
			Collections.sort(processed, cmp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void fillData() {
		clearData();
		
		String where = getWhere();
		DbReader reader = new DbReader();
		DbWriter.checkDBTable(OrdDlv.class);
		
		DataTraveler.travel(OrdDlv.class, new Travel<OrdDlv>() {
			
			@Override
			public boolean travel(DataTraveler<OrdDlv> item) {
				
				OrderRequest decision = OrderRequestImpl.inflateDecision(item.data.created.getTime());
				
				if (decision != null){
					decisions.put(item.data.created.getTime(), decision);
					processed.add(item.data);
				}else
					waiting.add(item.data);
				
				item.data = new OrdDlv();
				return true;
			}}, where);
		
		reader.close();
		
		if (sortField != null && sortField.trim().length() > 0)
			sort(sortField);
	}

	private void clearData() {
		waiting.clear();
		decisions.clear();
		processed.clear();
	}

	private String getWhere() {
		StringBuilder sb = new StringBuilder();
		sb.append("created >= ").append(Util.getDate().getTime());
		
		return sb.toString();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		switch(groupPosition){
		case 0: return waiting.get(childPosition);
		case 1: return processed.get(childPosition);
		default: return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {	return 0; }

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.orddlvrow, null);
		
		OrdDlv orddlv = (OrdDlv) getChild(groupPosition, childPosition);
		
		if(orddlv != null){
			((TextView)view.findViewById(R.id.tvAgent)).setText(orddlv.agent);
			((TextView)view.findViewById(R.id.tvDate)).setText(Util.simpleDateFormat.format(orddlv.created));
			((TextView)view.findViewById(R.id.tvOrg)).setText(orddlv.org);
			
			TextView tv = ((TextView)view.findViewById(R.id.tvSum)); 
			tv.setText(Util.IntToScaleStr(orddlv.sum, Consts.SUM_SCALE));
			tv.setOnClickListener(sumOnClick);
			tv.setTag(orddlv.created);
			tv = ((TextView)view.findViewById(R.id.tvSumD)); 
			tv.setText(Util.IntToScaleStr(orddlv.sumd, Consts.SUM_SCALE));
			tv.setOnClickListener(debtOnClick);
			tv.setTag(orddlv);
			
			LinearLayout llDecision = (LinearLayout) view.findViewById(R.id.llDecision);
			
			if(decisions.containsKey(orddlv.created.getTime())){
				llDecision.setVisibility(View.VISIBLE);
				OrderRequest d = decisions.get(orddlv.created.getTime());
				tv = (TextView) view.findViewById(R.id.tvDecision);
				tv.setText(DecisionHelper.getDecisionText(view.getContext(), d.decision));
				tv = (TextView) view.findViewById(R.id.tvRemark);
				tv.setText(d.remark);
			}else
				llDecision.setVisibility(View.GONE);
		}
		
		view.setBackgroundResource(childPosition % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector);
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		switch(groupPosition){
		case 0: return waiting.size();
		case 1: return processed.size();
		default: return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {	return group[groupPosition]; }

	@Override
	public int getGroupCount() { return group.length; }

	@Override
	public long getGroupId(int groupPosition) {	return groupPosition; }

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, android.R.layout.simple_expandable_list_item_1, null);
		
		String group = (String) getGroup(groupPosition);
		((TextView)convertView).setText(group);
		
		convertView.setBackgroundResource(R.drawable.list_grey_selector );
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {	return false; }

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) { return true;	}
}
