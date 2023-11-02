package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Payment;
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
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		Payment p = null;
		String str;
		int color;
		TextView tv;
		
		DataObject dobj = doc.getData();
		if( dobj instanceof Payment )
			p = (Payment)dobj;

		if( p != null ) {
			Date blueDate;
			Date redDate;
			
			Calendar c = Calendar.getInstance();
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, -1);
			redDate = c.getTime();
			
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, 3);
			blueDate = c.getTime();
			
			color = Color.GREEN;
			if( p.sum > 0 ) {
				color = (p.date.compareTo(redDate) <= 0) ?
						Color.RED : (p.date.compareTo(blueDate) <= 0 ) ?
						Color.BLUE :
						Color.GREEN;
			}
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setTextColor(color);
			str = Util.simpleDateFormat.format(p.date);
			tv.setText(str);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(p.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv.setTextColor(color);
						
			tv = (TextView)view.findViewById(R.id.tvOther);
			tv.setText(p.number);
			tv.setTextColor(color);
			
			return;
		}

		DeliveryEx d = null;
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;

		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		color = (d.sumD > 0 && d.payDate.compareTo(new Date()) < 0) ? Color.RED : Color.GREEN;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(d.number);
		tv.setTextColor(color);
	}
}
