package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
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
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class Plans extends RegDurationActivity {

	public static void open(Context ctx) {
		Intent i = new Intent(ctx, Plans.class);
		ctx.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ListView lv = (ListView)findViewById(R.id.lvPlans);
		lv.setAdapter(new PlansAdapter());
	}
	
	class PlansAdapter extends BaseAdapter {
		
		ArrayList<Plan> plans = new ArrayList<Plan>();
		
		PlansAdapter() {
			DbReader r = new DbReader();
			Plan p = new Plan();
			boolean bdo = r.select(p, DataObjectInfo.getInstance().getTableName(p.getClass()), null, "date, name");
			while( bdo ) {
				plans.add(p);
				p = new Plan();
				bdo = r.selectNext(p);
			}
			r.close();
		}

		@Override public int getCount() { return plans.size(); }

		@Override public Object getItem(int arg0) { return (arg0 < plans.size()) ? plans.get(arg0) : null; }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			Plan p = (Plan) getItem(position);
			if( p == null )
				return null;
			if( view == null )
				view = View.inflate(Plans.this, R.layout.plan_row, null);
			
			String txt;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(p.name);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			txt = sdf.format(p.date);
			txt += "\n";
			int pc = (int)((long)p.fact * Consts.SUM_SCALE * 10 / p.plan);
			txt += Util.IntToScaleStr(pc, 10, Util.DEC_DELIM, false);
			tv.setText(txt);
			
			tv = (TextView)view.findViewById(R.id.tvPlan);
			txt = Util.spacingDigitGroup(Util.IntToScaleStr(p.plan, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			txt += "\n";
			txt += Util.spacingDigitGroup(Util.IntToScaleStr(p.fact, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv.setText(txt);
			return view;
		}
		
	}
}
