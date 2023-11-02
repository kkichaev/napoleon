package com.grsoft.napoleon.plans;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.plans.dataobjects.impl.PlanImpl;
import com.grsoft.napoleon.plans.documents.PlanDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class Plans extends BaseActivity {
	private ListView lvPlans;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans);
		
		lvPlans = (ListView) findViewById(R.id.lvPlan);
		PlanDoc.instance().refreshFact();
	}
	
	public static void open(Context context){
		Intent intent = new Intent(context, Plans.class);
		context.startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		lvPlans.setAdapter(new PlanAdapter(this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		DocumentsAdapter adapter = (DocumentsAdapter) lvPlans.getAdapter();
		if (adapter != null)
			adapter.close();
	}
	
	class PlanAdapter extends DocumentsAdapter{

		protected PlanAdapter(Context context) {
			super(context, PlanDoc.instance(), "", "", R.layout.plans_row);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			PlanImpl plan = (PlanImpl) getItem(position);
			if (plan != null){
				int percent = plan.getData().fact / (plan.getData().plan / (100 * Consts.SUM_SCALE));
				int backgroundColor = getBackgroundColor(percent / Consts.SUM_SCALE);
				
				TextView tvPercent = (TextView) view.findViewById(R.id.tvPercent);
				
				if (tvPercent != null){
					tvPercent.setText(Util.IntToScaleStr(percent, Consts.SUM_SCALE));
					tvPercent.setBackgroundColor(backgroundColor);
				}
				
				 
				TextView tvName = (TextView) view.findViewById(R.id.tvName);
			
				if(tvName != null){
					tvName.setText(plan.getData().text);
					tvName.setBackgroundColor(backgroundColor);
				}
				
				TextView tvPlan = (TextView) view.findViewById(R.id.tvPlan);
				
				if (tvPlan != null){
					tvPlan.setText(Util.IntToScaleStr(plan.getData().plan, Consts.SUM_SCALE));
					tvPlan.setBackgroundColor(backgroundColor);
				}
				
				TextView tvFact = (TextView) view.findViewById(R.id.tvFact);
				
				if (tvFact != null){
					tvFact.setText(Util.IntToScaleStr( plan.getData().fact, Consts.SUM_SCALE));
					tvFact.setBackgroundColor(backgroundColor);
				}
				
				TextView tvRange = (TextView) view.findViewById(R.id.tvRange);
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
				
				if (tvRange != null){
					tvRange.setText(String.format("с: %s по: %s", sdf.format(plan.getData().from), 
							sdf.format(plan.getData().till)));
					tvRange.setBackgroundColor(backgroundColor);
				}
			}
		}
		
		private int getBackgroundColor(int percent){
			Context c = super.getContext();
			final int LOW = 33;
			final int MED = 66;
			
			int color = (percent <= LOW) ? R.color.salmon : 
						(percent <= MED) ? R.color.lightskyblue :
						R.color.palegreen;
			return c.getResources().getColor(color);
		}
	}
	
}
