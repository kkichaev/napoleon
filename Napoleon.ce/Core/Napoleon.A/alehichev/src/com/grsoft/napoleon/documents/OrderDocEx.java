package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.Napoleon;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;

import android.app.Activity;
import android.view.View;

public class OrderDocEx extends OrderDoc {
	static public void init() {
		instance = new OrderDocEx();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		if( documentsView instanceof Napoleon ) {
			View v = documentsView.findViewById(R.id.tvMainDocValColTitle);
			if( v != null )
				v.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		if( documentsView instanceof Napoleon ) {
			View v = documentsView.findViewById(R.id.tvMainDocValColTitle);
			if( v != null )
				v.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void setMainView(View view, LinesCountController linesController, OrgImpl orgImpl, OrgSumImpl orgSumImpl) {
		super.setMainView(view, linesController, orgImpl, orgSumImpl);
		View v = view.findViewById(R.id.tvOrgSum);
		if( v != null )
			v.setVisibility(View.GONE);
	}
	
	@Override
	public void updateTotalSum(Activity activity, int sum, int weight, int count, int textViewId) {
		//super.updateTotalSum(activity, sum, weight, count, textViewId);
		
		View tvTotalSum = activity.findViewById(textViewId);
		if( tvTotalSum != null )
			tvTotalSum.setVisibility(View.GONE);
	}
}
