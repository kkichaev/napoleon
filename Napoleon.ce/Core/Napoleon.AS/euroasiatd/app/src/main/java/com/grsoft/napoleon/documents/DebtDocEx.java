package com.grsoft.napoleon.documents;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliverySumImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.DocumentsEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {
	
	
	public static int NOTICE_DAY_COUNT = 3;

	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");
		
		if (documentsView instanceof DocumentsEx)
			((DocumentsEx)documentsView).updateOrgInfo(false);
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null ) {
			tv.setVisibility(View.VISIBLE);
			tv.setText("Сумма/Долг");
		}
		
		if (documentsView instanceof DocumentsEx)
			((DocumentsEx)documentsView).updateOrgInfo(true);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;

		if( d == null )
			return;

		String str;
		Date now = new Date();
		
		
		int color = (d.sumD > 0 && d.payDate.compareTo(now) < 0) ? Color.RED :
			(d.sumD > 0 && DatePeriod.daysDiff(now, d.payDate) < NOTICE_DAY_COUNT) ? view.getContext().getResources().getColor(R.color.dlv_notice_color) :
			Color.BLACK;
		
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		str = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		str += "\n";
		str += Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv.setVisibility(View.VISIBLE);
		tv.setText(str);
		tv.setTextColor(color);

		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
	
	private DeliverySumImpl dlvSum = new DeliverySumImpl();
	
	@Override
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl) {
		StringBuilder sb = new StringBuilder();
		
		if (dlvSum.read("id", orgSumImpl.getData().id)) {
			sb.append("<font color='red'>");
			sb.append(Util.IntToScaleStr(dlvSum.getData().dsum, Consts.SUM_SCALE));
			sb.append("</font><br>");
			
			sb.append("<font color='#FF840A'>");
			sb.append(Util.IntToScaleStr(dlvSum.getData().dsum2, Consts.SUM_SCALE));
			sb.append("</font>");
		}
		
		return sb.toString();
	}
}
