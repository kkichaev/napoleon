package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Plan;
import com.grsoft.view.BaseActivity;

public class Plans extends BaseActivity {
	ArrayList<Plan> plans = new ArrayList<Plan>();
	
	public static void open(Context c) {
		Intent i = new Intent(c, Plans.class);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans);
		
		String table = DataObjectInfo.getInstance().getTableName(Plan.class);
		DbReader r = new DbReader();
		Plan p = new Plan();
		boolean bdo = r.select(p, table, null, "name");
		while(bdo) {
			plans.add(p);
			p = new Plan();
			bdo = r.selectNext(p);
		}
		r.close();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.addHeaderView(View.inflate(this, R.layout.plan_head, null));
		lv.setAdapter(new Adapter());
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return plans.size(); }
		@Override public Object getItem(int arg0) { return arg0 < plans.size() ? plans.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			Plan p = (Plan)getItem(arg0);
			if( p == null )
				return null;
			
			if( view == null )
				view = View.inflate(Plans.this, R.layout.plan_row, null);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(p.name);

			tv = (TextView)view.findViewById(R.id.tvPrc);
			tv.setText(p.procent);

			tv = (TextView)view.findViewById(R.id.tvPlan);
			tv.setText(p.plan);

			tv = (TextView)view.findViewById(R.id.tvFact);
			tv.setText(p.fact);

			return view;
		}
		
	}
}
