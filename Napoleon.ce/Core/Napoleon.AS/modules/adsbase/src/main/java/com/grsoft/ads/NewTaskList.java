package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


public class NewTaskList extends BaseFragmentActivity{
	public static Class<? extends BaseFragmentActivity> activity = NewTaskList.class;
	
	private ListView list;
	
	public static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);
	}
	
	@Override
	protected void inflateView() {
		list = (ListView) findViewById(R.id.list);
	}

	@Override
	protected void initView() {
		list.setAdapter(new Adapter(this));
		list.setOnItemClickListener(onItemClick());
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Adapter a = (Adapter) list.getAdapter();
		if (a != null){
			a.load();
			a.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	private OnItemClickListener onItemClick() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskQuery t = (TaskQuery) parent.getItemAtPosition(position);
				TaskPreview.open((Activity) view.getContext(), t.taskid);
			}};
	}

	@Override
	protected int getLayoutID() { return R.layout.newtasklist; }
	
	private static class Adapter extends ListBaseAdapter{
		private List<TaskQuery> data = new ArrayList<TaskQuery>();
		private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		public Adapter(Context context){
			super(context);
			this.context = context;
		}

		public void load() {
			data.clear();
			final Class<? extends DataObject> dataType = DbObject.getDataType(TaskQuery.class);
			String where = "solution = 0";
			
			DataTraveler.travel(dataType, new DataTraveler.Travel<TaskQuery>(true) {
				@Override
				public boolean travel(DataTraveler<TaskQuery> item) {
					if(item.data != null)
						data.add(item.data);
					return true;
				}}, where);
			
			Collections.sort(data, new Comparator<TaskQuery>() {
				@Override public int compare(TaskQuery lhs, TaskQuery rhs) { return lhs.start.compareTo(rhs.start); }});
		}

		@Override
		public int getCount() { return data.size();
		}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0; }

		@Override
		protected int getLayoutID() { return R.layout.newtasklistrow; }

		@Override
		protected int initView(View view, Object item) {
			TaskQuery t = (TaskQuery) item;
			
			if(t != null){
				TextView tv = (TextView) view.findViewById(R.id.tvDate);
				if(tv != null)
					tv.setText(Util.simpleDateFormat.format(t.start));
				
				tv = (TextView) view.findViewById(R.id.tvTime);
				
				if(tv != null)
					tv.setText(String.format("%s - %s", sdf.format(t.start), sdf.format(t.finish)));
				
				tv = (TextView)view.findViewById(R.id.tvText);
				
				if(tv != null)
					tv.setText(t.text);
			}
			return 0;
		}
	}
}
