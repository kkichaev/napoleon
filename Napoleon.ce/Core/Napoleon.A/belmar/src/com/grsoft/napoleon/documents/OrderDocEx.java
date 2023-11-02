package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.text.Html;
import android.view.View;
import android.widget.Adapter;

public class OrderDocEx extends OrderDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("OrderDoc уже создан!");
		instance = new OrderDocEx();
	}

	public OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		int color = getViewTextColor(view.getContext(), doc);
		updTextItem(view, R.id.tvDate, doc.getDate() == null ? view.getContext().getString(R.string.doc_error) : getDateDocText(doc), color, null);
		updTextItem(view, R.id.tvSum, getSumWeight(doc), color, new ViewUpdater() {
			@Override public void update(View v) { v.setVisibility(View.VISIBLE); }});
		updTextItem(view, R.id.tvOther, Html.fromHtml(doc.getDescription(view.getContext())), color, null);
	}

	protected String getSumWeight(Document<?> doc) {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
		sb.append("\n");
		sb.append(Util.IntToScaleWStr(((OrderImpl)doc).weight(), Consts.WEIGHT_SCALE, 3, false));
		return sb.toString();
	}
}
