package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.impl.PlanImpl;

public class Planes extends ListActivity {

	public static void open(Context context){
		Intent intent = new Intent(context, Planes.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planes);
		setListAdapter(new PlanesAdapter(this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		PlanesAdapter adapter = (PlanesAdapter) getListAdapter();
		if (adapter != null)
			adapter.close();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PlanesAdapter adapter = (PlanesAdapter) getListAdapter();
		if (adapter != null){
			PlanImpl planImpl = new PlanImpl();
			if (planImpl.read((Long) adapter.getItem(position)))
				planImpl.open(this);
			planImpl.close();
		}
	}
}

class PlanesAdapter extends BaseAdapter{
	private List<Long> rowids = new ArrayList<Long>();
	private PlanImpl planImpl = new PlanImpl();
	private Context context;
	
	public PlanesAdapter(Context context){
		this.context = context;
		Cursor c = null;
		
		try{
			c = DataBaseManager.getDataBase().
				query(DataObjectInfo.getInstance().getTableName(Plan.class), 
						new String[] {"rowid"}, null, null, null, null, null);
			if (c.moveToFirst()){
				do{
					rowids.add(c.getLong(0));
				}while(c.moveToNext());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
	}
	
	@Override
	public int getCount() {
		return rowids.size();
	}

	@Override
	public Object getItem(int position) {
		return rowids.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.plan_row, null);

		if (planImpl.read((Long) getItem(position))){
			TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
			tvText.setText(planImpl.getData().name);
		}
		
		return convertView;
	}
	
	public void close(){
		planImpl.close();
	}
}
