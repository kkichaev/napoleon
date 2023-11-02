package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
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
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		if( tv != null )
			tv.setText("");
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Отгрузка\nОплата");
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		if( tv != null )
			tv.setText("№ док\nПросрочено");
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма\nДолг");
	}
	
	String getDays(Date d1, Date d2) {
		long val = d1.getTime() - d2.getTime();
		if( val < 0 )
			return "";
		
		return Integer.toString((int) (val / (1000 * 3600 * 24))); 
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;

		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		Date checkDate = new Date();
		String str;
		int color = (d.sumD > 0 && d.payDate.compareTo(checkDate) < 0) ? Color.RED : Color.BLACK;
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
		tv.setText(str);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		str = d.number + "\n" + getDays(checkDate, d.payDate);
		tv.setText(str);
		tv.setTextColor(color);
	}
}
