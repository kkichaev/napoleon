package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		instance = new DebtDocEx();
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText(R.string.date);
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText(R.string.sum);
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText(R.string.date_pay);
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText(R.string.sum_dolg);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			drawDelivery(view, (DeliveryEx)dobj);
		else if( dobj instanceof Payment) {
			int color = getColor(doc.sum(), doc.getDate());
			
			((TextView)view.findViewById(R.id.tvDate)).setTextColor(color);
			((TextView)view.findViewById(R.id.tvSum)).setTextColor(color);
			((TextView)view.findViewById(R.id.tvOther)).setTextColor(color);
		}
	}
	
	int getColor(int sum, Date date) {
		int color = Color.BLACK;
		if(sum > 0) {			
			Date blueDate;
			Date redDate;
			
			Calendar c = Calendar.getInstance();
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, -1);
			redDate = c.getTime();
			
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, 3);
			blueDate = c.getTime();
			
			if( date == null || date.compareTo(redDate) <=0 )
				color = Color.RED;
			else if( date != null && date.compareTo(blueDate) <=0 )
				color = Color.BLUE;
		}
		return color;
	}

	private void drawDelivery(View view, DeliveryEx d) {
		String str;
		int color = getColor(d.sumD, d.payDate);
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		str = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n" +
				Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv.setText(str);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
}
