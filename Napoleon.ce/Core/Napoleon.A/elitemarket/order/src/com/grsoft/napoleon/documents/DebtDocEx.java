package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
//		if( instance != null )
//			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx(DOC_NAME, Debt.class);
	}
	
	protected DebtDocEx(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
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

		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;

		if( d == null )
			return;

		String str;
		int color = (d.sumD > 0 && d.payDate.compareTo(Util.getDate()) < 0) ? Color.RED : Color.BLACK;
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
}
