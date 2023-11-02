package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc {
	public int isum;

	static public void init() {
		instance = new DebtDocEx();
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		int w = 4 * metrics.widthPixels / 10;
		TextView tv = (TextView) view.findViewById(R.id.tvOther);
		if( tv != null )
			tv.setWidth(w);
		
		tv = (TextView) view.findViewById(R.id.tvDate);
		if( tv != null )
			tv.setWidth(w/2);

		tv = (TextView) view.findViewById(R.id.tvSumDoc);
		if( tv != null ) {
			tv.setWidth(w/2);
			tv.setText("");
			tv.setGravity(Gravity.RIGHT);
			if (doc instanceof DeliveryImpl) {
				long sumDoc = ((DeliveryImpl)doc).getData().sum();
				tv.setText(Util.IntToScaleWStr(sumDoc, Consts.SUM_SCALE, 2, false));
			}
		}
		
		tv = (TextView) view.findViewById(R.id.tvSum);
		if( tv != null )
			tv.setWidth(w/2);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setVisibility(View.VISIBLE);
		tv = (TextView)documentsView.findViewById(R.id.DebetTitle);
		if( tv != null )
			tv.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv = (TextView)documentsView.findViewById(R.id.DebetTitle);
		if( tv != null )
			tv.setVisibility(View.GONE);
	}

	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 )
			whereStr += " AND " + where;
		
		return new DebtDocListEx(whereStr, order, LoadDelivery);
	}
}
