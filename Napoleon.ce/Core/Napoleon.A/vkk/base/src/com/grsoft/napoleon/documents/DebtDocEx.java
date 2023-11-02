package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		instance = new DebtDocEx();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		if( tv != null )
			tv.setText("Номер/Агент");
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
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		if( doc instanceof PaymentImpl ) {
			Date minDate = new Date(71, 0, 1);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			long redValue = c.getTime().getTime();
			long ylwValue = redValue + 3l * 24 * 3600 * 1000;			
			long blwDate = minDate.getTime();					

			int color = Color.BLACK;
			
			PaymentEx pe = (PaymentEx) doc.getData();
			long payDate = pe.payDate.getTime();
			if( payDate > blwDate ) {
				if( payDate < ylwValue )
					color= Color.BLUE;
				if( payDate < redValue )
					color = Color.RED;
			}
			
			String text;
			TextView tv = (TextView)view.findViewById(R.id.tvDate);			
			if (doc.getDate() == null)
				tv.setText(R.string.doc_error);
			else {
				text = Util.simpleDateFormat.format(doc.getDate());
				text += "<br>";
				text += Util.simpleDateFormat.format(pe.payDate);
				tv.setText(Html.fromHtml(text));
			}
			
			tv.setBackgroundColor(Color.WHITE);
			tv.setTextColor(color);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setVisibility(View.VISIBLE);
			tv.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
			tv.setBackgroundColor(Color.WHITE);
			tv.setTextColor(color);
			
			tv = (TextView)view.findViewById(R.id.tvOther);
			text = pe.number + "<br>" + pe.agent;
			tv.setText(Html.fromHtml(text));		
			tv.setBackgroundColor(Color.WHITE);
			tv.setTextColor(color);
			
			view.setBackgroundColor(Color.WHITE);
		} else
			super.setView(adapter, view, doc);
	}
}
