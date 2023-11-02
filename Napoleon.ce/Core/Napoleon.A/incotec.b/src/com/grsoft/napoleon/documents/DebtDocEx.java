package com.grsoft.napoleon.documents;

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
//		super.setView(adapter, view, doc);

		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;

		if( d == null )
			return;

		String str;
		int color = Util.GrServerColorToSystem(d.color);
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
		tv.setTextColor(color);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		str = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		str += "\n";
		str += Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv.setVisibility(View.VISIBLE);
		tv.setText(str);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		if(tv != null) {
			tv.setText(d.number);
			tv.setTextColor(color);
		}
	}
}
