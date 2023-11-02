package com.grsoft.napoleon.documents;

import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
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
	
	public int getColor(Context context, Date payDate){
		int result = Color.BLACK;
		
		Date today = Util.getDate();
		
		if (payDate.compareTo(today) < 0)
			result = Color.RED;
//		else if (DatePeriod.daysDiff(today, payDate) <= 7)
//			result = Color.BLUE;
		
		return result;
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;

		if( d == null )
			return;

		Date today = Util.getDate();
		boolean expired = d.payDate.compareTo(today) < 0;
		
		String str;
		int color = d.sumD > 0 && expired ? Color.RED : Color.BLACK;
		
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		
		if (expired) {
			str = Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false);
			str += "\n";
			str += DatePeriod.daysDiff(d.payDate, today);
			tv.setText(str);
		}
		
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
}
