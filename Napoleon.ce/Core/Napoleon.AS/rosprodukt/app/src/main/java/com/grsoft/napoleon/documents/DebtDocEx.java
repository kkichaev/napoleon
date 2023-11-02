package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Ret1c;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	Date minDate;
	protected DebtDocEx() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
	}

	public static void init() {
		instance = new DebtDocEx();
	}

	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата отгр/Оплаты");
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата");
	}
	
	@Override
	protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
		return new DebtDocListEx(where, order, LoadDelivery);
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
		TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
		TextView tvOther = (TextView)view.findViewById(R.id.tvOther);

		DataObject data = doc.getData();
		int color = Color.BLACK;
		int backColor = Color.TRANSPARENT;
		if( data instanceof Ret1c ) {
			Ret1c d = (Ret1c)data;

			String text = Util.simpleDateFormat.format(d.date);
			tvDate.setText(text);
			
			tvSum.setVisibility(View.VISIBLE);
			tvSum.setText(Html.fromHtml("<i>" + Util.IntToScaleWStr(d.sum(), Consts.SUM_SCALE, 2, false) + "</i>"));
			tvOther.setText(Html.fromHtml("<i>возврат<br/>" + doc.getNumber()));		
		} else  if( data instanceof DeliveryEx ) {
			DeliveryEx d = (DeliveryEx)data;

			Date check = new Date();
			if( d.payDate.before(check) && d.payDate.after(minDate) && d.sumD > 0 )
				color = Color.RED;
						
			String text = Util.simpleDateFormat.format(d.date) + "\n" + Util.simpleDateFormat.format(d.payDate);
			tvDate.setText(text);
			tvSum.setVisibility(View.VISIBLE);
			tvSum.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
			tvOther.setText(doc.getDescription(view.getContext()));		

			backColor = (d.color == 0) ? Color.TRANSPARENT : (Util.GrServerColorToSystem(d.color) | 0x88000000);
		} else 	{	
			super.setView(adapter, view, doc);
		}
		
		tvDate.setTextColor(color);
		tvSum.setTextColor(color);
		tvOther.setTextColor(color);

		view.setBackgroundColor(backColor);
	}
}
