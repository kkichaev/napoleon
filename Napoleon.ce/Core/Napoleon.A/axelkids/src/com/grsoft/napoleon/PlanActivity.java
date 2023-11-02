package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Plan;
import com.grsoft.napoleon.impl.PriceGroupImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PlanActivity extends Activity {
	private ListView list;
	private PriceGroupImpl priceGroup = new PriceGroupImpl(); 
	
	public static void open(Context context){
		Intent intent = new Intent(context, PlanActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(new Adapter());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		priceGroup.close();
	}
	
	class Adapter extends BaseAdapter{
		List<Plan> data = new ArrayList<Plan>();
		
		public Adapter(){
			DbReader reader = new DbReader();
			Plan d = new Plan();
			
			boolean bdo = reader.select(d, DataObjectInfo.getInstance().getTableName(d.getClass()), null);
			while(bdo){
				data.add((Plan) d.clone());
				bdo = reader.selectNext(d);
			}
			
			reader.close();
		}
		
		@Override
		public int getCount() { return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) {return 0;}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(PlanActivity.this, R.layout.plan_row, null);
			
			Plan row = (Plan) getItem(position);
			priceGroup.getData().id = row.id;
			priceGroup.read();
			int percent = (int)((double)row.fact / row.plan * 100);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(priceGroup.getData().name + "<br>" + percent + " %"));
			tv = (TextView) view.findViewById(R.id.tvPlan);
			tv.setText(Util.IntToScaleStr(row.plan, Consts.SUM_SCALE));
			tv = (TextView) view.findViewById(R.id.tvFact);
			tv.setText(Util.IntToScaleStr(row.fact, Consts.SUM_SCALE));
			
			return view;
		}
		
	}
}

