package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		if (doc instanceof DeliveryImpl){
			DeliveryEx dlv = (DeliveryEx)doc.getData();
			TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
			
			if (doc.getDate() == null || dlv.payDate == null)
				tvDate.setText(R.string.doc_error);
			else
				tvDate.setText(Html.fromHtml(Util.simpleDateFormat.format(doc.getDate()) + "<br>" +
					Util.simpleDateFormat.format(dlv.payDate)));
			
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			tvSum.setText(Html.fromHtml(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false) + "<br>" +
					Util.IntToScaleWStr(dlv.sum(), Consts.SUM_SCALE, 2, false)));
			
			int cl = Util.GrServerColorToSystem(dlv.color);
			tvDate.setTextColor(cl);
			tvSum.setTextColor(cl);
			TextView tv = (TextView)view.findViewById(R.id.tvOther);
			tv.setTextColor(cl);
		}
	}
	
	public DocList dlvList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		return new DebtDocList(whereStr, order, LoadDelivery){
			@Override
			protected void init(String where, String order, boolean loadDelivery) {
				deliveries = (loadDelivery) ? new DocList(BalanceDelivery.class, where, order) : null;
				payments = null;
			}
		};
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		ImageView iv = (ImageView) documentsView.findViewById(R.id.ivFilter);
		
		if(iv != null)
			iv.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		
		ImageView iv = (ImageView) documentsView.findViewById(R.id.ivFilter);
		
		if(iv != null)
			iv.setVisibility(View.GONE);
	}
	
	@Override public int getDocTitle() { return R.string.shipped; }
}
