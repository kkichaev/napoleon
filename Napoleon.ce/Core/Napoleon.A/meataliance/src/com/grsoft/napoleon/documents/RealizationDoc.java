package com.grsoft.napoleon.documents;

import java.util.Date;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import com.grsoft.dataobjects.Realization;
import com.grsoft.dataobjects.impl.RealizationImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class RealizationDoc extends DocType {
	static public final String DOC_NAME = "Realization";
	static public final String OBJ_NAME = "Realization";
	
	static protected RealizationDoc instance = null;
	
	protected RealizationDoc(String name, String objname, Class<? extends Document<?>> docClass) {
		super(name, objname, docClass);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new RealizationDoc(DOC_NAME, OBJ_NAME, RealizationImpl.class);
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.realization; }
	@Override public int getDocTitle() { return R.string.realization_doc; }
	
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
			tv.setText("Дата");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		Realization r = (Realization) doc.getData();
		
		String str;
		int color = (r.sumD > 0 && r.payDate.compareTo(new Date()) < 0) ? Color.RED : Color.BLACK;
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(r.date);
//		str += "\n";
//		str += Util.simpleDateFormat.format(r.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}

}
