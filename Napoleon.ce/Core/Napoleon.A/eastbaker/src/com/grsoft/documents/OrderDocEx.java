package com.grsoft.documents;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.text.Html;
import android.view.View;
import android.widget.Adapter;

public class OrderDocEx extends OrderDoc {
	public static void init() {
		instance = new OrderDocEx();
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		int color = getViewTextColor(view.getContext(), doc);
		super.setView(adapter, view, doc);
		
		String text = "<i>" + Integer.toString(((OrderImpl)doc).qty()) + "רע.</i><br/>" + 
				Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false); 
		updTextItem(view, R.id.tvSum, Html.fromHtml(text), color, new ViewUpdater() {
			@Override public void update(View v) { v.setVisibility(View.VISIBLE); }});
	}
}
