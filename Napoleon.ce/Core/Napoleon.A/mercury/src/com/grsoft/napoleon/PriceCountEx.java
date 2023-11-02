package com.grsoft.napoleon;

import android.os.AsyncTask;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.OrderImpl;


public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	OrderImpl order = null;
	TextView tvPlanPrev;
	TextView tvPlan;
	
	int fact = 0;
	int plan = 0;
	int progress = 0;
	
	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		tvPlanPrev = (TextView) findViewById(R.id.tvPlanPrev);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(document instanceof OrderImpl){
			order = (OrderImpl) document;
			planIni.execute((Void)null);
		}
	}
	
	AsyncTask<Void, Void, Void> planIni = new AsyncTask<Void, Void, Void>(){

		@Override
		protected Void doInBackground(Void... params) {
			PlanHelper.init(order.getData().created);
			return null;
		}
		
		protected void onPostExecute(Void result) {
			String id = price.getData().id;
			plan = PlanHelper.getPlanQty(id);
			fact = PlanHelper.getOrdQty(id);
			int percent = plan == 0 ? 0 : (int) Math.round(((double) fact / plan * 100));
			updatePlanBeforeView(percent);
			updatePlanAfterView();
		};
	};

	protected void updatePlanBeforeView(int percent) { tvPlanPrev.setText(getString(R.string.plan_before, percent)); }
	
	protected void updatePlanAfterView(){
		int percent = calclPlanAfter();
		tvPlan.setText(getString(R.string.plan_after, percent));
	}
	
	protected int calclPlanAfter() {
		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		
		int after = fact + qty;
		int percent = plan == 0 ? 0 : (int) Math.round(((double) after / plan * 100));
		
		return percent;
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		updatePlanAfterView();
	}
}
