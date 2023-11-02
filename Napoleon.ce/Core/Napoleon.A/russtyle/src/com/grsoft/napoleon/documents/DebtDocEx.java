package com.grsoft.napoleon.documents;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;
		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		int color = (d.payDate.compareTo(new Date()) < 0 ) ? Color.RED : Color.BLACK;
		updTextItem(view, R.id.tvOther, d.number, color, null);
		updTextItem(view, R.id.tvDate, sf.format(d.date) + "\n" + sf.format(d.payDate), color, null);
		String text = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n" +
			Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		updTextItem(view, R.id.tvSum, text, color, null);
	}
}
