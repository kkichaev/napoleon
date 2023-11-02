package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.napoleon.DocumentsEx;
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
		if( DocumentsEx.class.isAssignableFrom(documentsView.getClass())  ) {			
			tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
			tv.setText(R.string.sum);
	
			tv = (TextView)documentsView.findViewById(R.id.DocDate);
			tv.setText(R.string.date);
		} else
			super.viewClosed(documentsView);

		tv = (TextView)documentsView.findViewById(R.id.DocType);
		if( tv != null )
			tv.setVisibility(View.GONE);
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		int v = View.GONE; 
		if( DocumentsEx.class.isAssignableFrom(documentsView.getClass())  ) {
			v = View.VISIBLE;
	
			tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
			tv.setText("Сумма/Дней");
			tv.setVisibility(View.VISIBLE);
			
			tv = (TextView)documentsView.findViewById(R.id.DocDate);
			tv.setText("Накл/Оплата");
		} else
			super.viewOpened(documentsView);
		
		tv = (TextView)documentsView.findViewById(R.id.DocType);
		if( tv != null )
			tv.setVisibility(v);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		PaymentEx p = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof PaymentEx )
			p = (PaymentEx)dobj;
		
		if( p == null ) {
			super.setView(adapter, view, doc);
			return;
		}

		int color = Util.GrServerColorToSystem(p.color);
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		
		view.setBackgroundColor(color);

		String str;
		str = ((p.dlvDate == null) ? "" : Util.simpleDateFormat.format(p.dlvDate));
		str += "\n";
		str += Util.simpleDateFormat.format(p.date);
		tv.setText(str);
		tv.setBackgroundColor(Color.TRANSPARENT);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		str = Util.IntToScaleWStr(p.sum, Consts.SUM_SCALE, 2, false);
		str += "\n";
		str += ((p.delay > 0) ? Integer.toString(p.delay) : "0");
		tv.setText(str);
		tv.setBackgroundColor(Color.TRANSPARENT);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(p.number);		
		tv.setBackgroundColor(Color.TRANSPARENT);

		tv = (TextView)view.findViewById(R.id.tvType);
		tv.setText(p.type);		
		tv.setBackgroundColor(Color.TRANSPARENT);
	}
}
