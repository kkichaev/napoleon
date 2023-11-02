package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
//	boolean workedRefreshDocSum = false;
	
	
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
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма/Долг");
	}
	
	@Override
	public void refreshDocSum() throws com.grsoft.network.exception.RuntimeException {
	}
	
	public void updateFromCache(HashMap<String, Long> orgBalance) {
		writeSumMap(orgBalance, true);
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;

		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		int color = (d.sumD > 0 && d.payDate.compareTo(new Date()) < 0) ? Color.RED : Color.BLACK;
		String str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);		
					
		updTextItem(view, R.id.tvDate, str, color, null);		
		updTextItem(view, R.id.tvSum, Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false), color, null);		
		updTextItem(view, R.id.tvOther, Html.fromHtml(doc.getDescription(view.getContext())), color, null);		
	}
}

