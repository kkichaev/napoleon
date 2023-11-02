package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.ads.view.TaskAdapter;
import com.grsoft.ads.view.TimeLine;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class AdsFragment extends BaseFragment{
	public static final String DATE = "com.grsoft.ads.AdsFragment.DATE";
	protected Date date = new Date();
	private TimeLine timeLine;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
	private ScrollView scroll;
	private TextView tvDate;
	private TaskAdapter adapter;
	private SwipeRefreshLayout swipeView;

	public void refresh() {
		if(timeLine != null){
	        TaskAdapter adapter = ((TaskAdapter)timeLine.getAdapter());
	        if(adapter != null)
	        	adapter.reload(date);
			((TaskAdapter)timeLine.getAdapter()).notifyDataSetChanged();
			timeLine.invalidate();
		}
	}

	@Override
	protected int getLayoutID() { return R.layout.adsfragment; }

	@Override
	protected void inflateView(View view) {
		scroll = (ScrollView) view.findViewById(R.id.scroll);
		tvDate = (TextView) view.findViewById(R.id.tvDate);
		timeLine = (TimeLine) view.findViewById(R.id.timeLine);
		swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeview);
	}

	@Override
	protected void init() {
		Bundle args = getArguments();
		
		if(args != null)
			date = new Date(args.getLong(DATE));
			
		adapter = new TaskAdapter(getActivity());
		adapter.reload(date);
	}

	@Override
	protected void initView() {
		tvDate.setText(sdf.format(date));
		timeLine.setAdapter(adapter);
		scroll.post(scrollToNow());
		timeLine.setOnItemClickListener(onClick());
		timeLine.drawTimeRect(TaskPageAdapter.isSameData(date, Calendar.getInstance().getTime()));
		swipeView.setOnRefreshListener(onRefresh());
	}

	private OnRefreshListener onRefresh() {
		return new OnRefreshListener() {
			@Override public void onRefresh() {
				swipeView.setRefreshing(false);
				((Ads)getActivity()).sync();
			}
		};
	}

	protected OnItemClickListener onClick() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskQuery t = (TaskQuery) parent.getItemAtPosition(position);
				
				if(t != null){
					if(t.solution == TaskQuery.NEW)
						TaskPreview.open(getActivity(), t.taskid);
					else if(t.solution == TaskQuery.APPLY)
						TaskReadyToStart.open(getActivity(), t.taskid);
					else if (t.solution == TaskQuery.INWORK) 	
						TaskEdit.open(getActivity(), t.taskid);
				}
			}};
	}

	protected Runnable scrollToNow() {
		return new Runnable() {	@Override public void run() { scroll.scrollTo(0, timeLine.getOffsetForNow() - scroll.getHeight() / 2);} };
	}
}