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
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");

		tv = (TextView)documentsView.findViewById(R.id.DocDate);
		if( tv != null )
			tv.setText("Дата");

		tv = (TextView)documentsView.findViewById(R.id.tvInfoHead);
		if( tv != null )
			tv.setText("");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма/Долг");
		
		tv = (TextView)documentsView.findViewById(R.id.DocDate);
		if( tv != null )
			tv.setText("Дата/Оплата");

		tv = (TextView)documentsView.findViewById(R.id.tvInfoHead);
		if( tv != null )
			tv.setText("Номер/Агент");
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

		int color = (d.payDate.compareTo(new Date()) < 0 ) ? Color.RED : Color.BLACK;
		String str;
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setTextSize(16);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
		str = Util.IntToScaleWStr(d.sum(), Consts.SUM_SCALE, 2, false);
		str += "\n";
		str += Util.IntToScaleWStr(d.sumD, Consts.SUM_SCALE, 2, false);;
		tv.setTextSize(16);
		tv.setText(str);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
		str = d.number;
		str += "\n";
		str += d.agent;
		tv.setTextSize(16);
		tv.setText(str);
	}
}
