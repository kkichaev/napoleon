package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.TextLog;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.util.DatePeriod;

public class LogFragment extends Fragment {
	
	
	private ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.log, container, false);
		
		list = (ListView) result.findViewById(R.id.list);
		list.setDividerHeight(0);
		list.setAdapter(createAdapter());
		
		return result;
	}

	private ListAdapter createAdapter() { return new LFAdapter(getActivity(), (SelParam)getActivity());	}
	
	public void refresh() {
		LFAdapter adapter = (LFAdapter) list.getAdapter();
		adapter.load();
		adapter.notifyDataSetChanged();
	}
}

class LFAdapter extends BaseAdapter{
	private Context context;
	private SelParam param;
	private List<TextLog> data;
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
	
	public LFAdapter(Context context, SelParam param) {
		this.context = context;
		this.param = param;
		this.data = new ArrayList<TextLog>();
		load();
	}
	
	public void load(){
		data.clear();
		DatePeriod dp = DatePeriod.createRange(param.getDate(), DatePeriod.MIN_PER_DAY);
		StringBuilder where = new StringBuilder();
		where.append("userid='").append(param.getUserid()).append("' and ");
		where.append("date >= ").append(dp.begin.getTime()).append(" and date < ").append(dp.end.getTime());
		
		DataTraveler.travel(TextLog.class, new DataTraveler.Travel<TextLog>() {
			@Override
			public boolean travel(DataTraveler<TextLog> item) {
				data.add(item.data);
				item.data = new TextLog();
				return true;
			}}, where.toString()); 
	}
	
	@Override
	public int getCount() { return data.size();	}

	@Override
	public Object getItem(int pos) { return data.get(pos);	}

	@Override
	public long getItemId(int arg0) { return 0;	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		if (view == null)
			view = View.inflate(context, R.layout.log_row, null);

		TextLog item = (TextLog) getItem(pos);

		if (item != null) {
			((TextView) view.findViewById(R.id.tvName)).setText(item.text);
			((TextView) view.findViewById(R.id.tvDate)).setText(sdf.format(item.date));
		}

		view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector );
		return view;
	}
	
}
