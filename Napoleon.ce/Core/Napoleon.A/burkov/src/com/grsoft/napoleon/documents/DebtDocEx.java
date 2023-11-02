package com.grsoft.napoleon.documents;

import java.util.Date;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

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
		super.setView(adapter, view, doc);

		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;

		if( d == null )
			return;

		String str;
		int color = Color.BLACK;
		Date today = new Date();
		
		boolean isValidPayDate = d.payDate.getYear() > 100;
		if (d.sumD > 0 && isValidPayDate){
			if (isSameData(d.payDate, today))
				color = Color.MAGENTA;
			else if(d.payDate.compareTo(new Date()) < 0)
				color = Color.RED;
		}
		
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		
		if(isValidPayDate){
			str += "\n";
			str += Util.simpleDateFormat.format(d.payDate);
		}
		
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
	
	public static boolean isSameData(Date d1, Date d2) {
		final int HOUR_PART = 1000 * 60 * 60 * 60 * 24;
		return (d1.getTime() / HOUR_PART) == (d2.getTime() / HOUR_PART);
	}
}
