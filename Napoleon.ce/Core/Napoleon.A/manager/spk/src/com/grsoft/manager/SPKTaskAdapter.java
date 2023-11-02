package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SPKTaskAdapter extends BaseAdapter {
	private List<ManagerAgent> data = new ArrayList<ManagerAgent>();
	private Map<String, Integer> pendings = new HashMap<String, Integer> ();
	private Context context;
		
	public SPKTaskAdapter(Context context) {
		this.context = context;
		reload();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.task_row, null);
		
		ManagerAgent a = (ManagerAgent) getItem(position);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(a.name);
		
		tv = (TextView) view.findViewById(R.id.tvCount);
		int count = 0;
		
		if (pendings.containsKey(a.id))
			count = pendings.get(a.id);
		
		tv.setText(Integer.toString(count));
		
		return view;
	}
	
	public void reload() {
		data.clear();
		
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(true) {

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				data.add(item.data);
				return true;
			}
			
		}, null);
		
		Collections.sort(data, new Comparator<ManagerAgent>() {

			@Override
			public int compare(ManagerAgent lhs, ManagerAgent rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
		
		pendings.clear();
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery("SELECT agentid, count(*) as count FROM 'spktask'  WHERE status=0 group by agentid", null);
			
			while(c.moveToNext()) {
				String id = c.getString(c.getColumnIndex("agentid"));
				int val = c.getInt(c.getColumnIndex("count"));
				
				if (!pendings.containsKey(id))
					pendings.put(id, val);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (c != null)
				c.close();
		}
		
		
	}
	

}
