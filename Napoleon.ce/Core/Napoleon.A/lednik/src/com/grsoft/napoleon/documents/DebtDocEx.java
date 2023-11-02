package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	Date minDate;
	protected DebtDocEx() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
	}

	public static void init() {
		instance = new DebtDocEx();
	}

	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата отгр/Оплаты");
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата");
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DeliveryEx d = null;
		if( doc.getData() instanceof DeliveryEx )
			d = (DeliveryEx)doc.getData();
		
		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}

		Date check = new Date();
		int color = Color.BLACK;
		if( d.payDate.before(check) && d.payDate.after(minDate) && d.sumD > 0 )
			color = Color.RED;
		
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		String text = Util.simpleDateFormat.format(d.date) + "\n" + Util.simpleDateFormat.format(d.payDate);
		tv.setText(text);
		tv.setTextColor(color);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		tv.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(doc.getDescription(view.getContext()));		
		tv.setTextColor(color);
	}
}
