package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.TaskInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class TaskStartEdit extends Activity {
	protected CreatableDocument<? extends CreateDocDataObject> doc;
	private ListView list;
	private Spinner spGroup;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, TaskStartEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutid());
	
		list = (ListView) findViewById(R.id.list);
		spGroup = (Spinner) findViewById(R.id.spGroup);
		
		doc = (CreatableDocument<? extends CreateDocDataObject>) DocType.getCurDoc().create();
		doc.read(getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR));
		
		list.setDividerHeight(0);
		list.setAdapter(new ItemsAdapter());
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, TaskInfo.TASK_GROUP_KEY);
		String[] arr = sb.toString().split(TaskInfo.TASK_GROUP_DELIMITER);
		List<String> values = new ArrayList<String>();
		
		for(String s : arr)
			values.add(s);
		
		Collections.sort(values);
		values.add(0, "<Все>");
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spGroup.setAdapter(aa);
		spGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1, int pos, long arg3) {
				ItemsAdapter ls = (ItemsAdapter) list.getAdapter();
				if(pos == 0)
					ls.resetFilter();
				else
					ls.setFilter((String)adapter.getItemAtPosition(pos));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}});
	}

	protected int getLayoutid() {
		return R.layout.taskstartedit;
	}
	
	protected int getItemLayout(){ return R.layout.taskstartedit_row;	}
	
	class ItemsAdapter extends BaseAdapter{
		List<TaskInfo> data = new ArrayList<TaskInfo>();
		
		public ItemsAdapter(){
			fillData(null);
		}
		
		private void fillData(String filter){
			data.clear();
			
			StringBuilder where = new StringBuilder();
			where.append("id='").append(doc.getId()).append("' and ");
			where.append("date < ").append(Util.getDate().getTime()).append(" and ");
			where.append("(done = 0 or donedate = ").append(Util.getDate().getTime()).append(")");
			
			if(filter != null)
				where.append(" and idgr='").append(filter).append("'");
			
			DataTraveler.travel(TaskInfo.class, new DataTraveler.Travel<TaskInfo>(){

				@Override
				public boolean travel(DataTraveler<TaskInfo> item) {
					data.add(item.data);
					item.data = new TaskInfo();
					return true;
				}}, where.toString());
		}
		
		public void setFilter(String value) {
			fillData(value);
			notifyDataSetChanged();
		}

		public void resetFilter() {
			fillData(null);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() { return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(TaskStartEdit.this, getItemLayout() , null);
			
			TaskInfo ti = (TaskInfo) getItem(position);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvItem);
			tv.setText(ti.text);
			
			tv = (TextView) convertView.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(ti.date));
			
			updateView(ti, convertView);
			
			convertView.setBackgroundResource(position % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);
			
			return convertView;
		}
	}

	public void updateView(TaskInfo ti, View convertView) {}
	
}


