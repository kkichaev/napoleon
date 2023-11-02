package com.grsoft.napoleon.documents;

import java.text.SimpleDateFormat;

import android.app.Activity;
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
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;
		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		
		String text;
		TextView tv;			

		text = d.number;
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(text);
		
		text = sf.format(d.date) + "\n" + sf.format(d.payDate);
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText(text);

		text = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n" +
			Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(text);
	}
}
