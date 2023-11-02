package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public static void initialize() {
		instance = new DebtDocEx();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);

		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Дней");

	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);

		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		DataObject dobj = doc.getData();		
		if(dobj instanceof Delivery) {
			Delivery de = (Delivery)dobj;
			Date d = new Date();
			long diff = d.getTime() - de.payDate.getTime();
			diff /= (1000 * 86400);
			
			TextView tv = (TextView)view.findViewById(R.id.tvDate);
			String str = Util.simpleDateFormat.format(de.payDate) + "\n" + Long.toString(diff); 
			tv.setText(str);
	
			str = doc.getDescription(view.getContext());
			str += "<br>" + Util.simpleDateFormat.format(de.date);
			tv = (TextView)view.findViewById(R.id.tvOther);
			tv.setText(Html.fromHtml(str));		
		}
	}
}
