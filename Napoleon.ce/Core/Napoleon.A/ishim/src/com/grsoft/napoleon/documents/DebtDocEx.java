package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
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
			tv.setText(documentsView.getString(R.string.date));
//		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
//		if( tv != null )
//			tv.setText("");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
//		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
//		if( tv != null )
//			tv.setText("Статус/Просрочено");
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
		
		String str;
		boolean haveOutPay = (d.sumD > 0 && d.payDate != null && d.payDate.compareTo(new Date()) < 0);
		int color = haveOutPay ? Color.RED : Color.BLACK;
		TextView tv;
		
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
		str = d.number;
		if( haveOutPay ) {
			Date cdt = new Date();
			int diff = (int)((cdt.getTime() - d.payDate.getTime()) / (1000 * 3600 * 24));
			str += "\n" + Integer.toString(diff);
		}
		tv.setText(d.number);
		tv.setTextColor(color);
	}
}
