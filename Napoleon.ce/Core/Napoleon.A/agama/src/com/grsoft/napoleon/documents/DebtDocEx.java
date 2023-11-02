package com.grsoft.napoleon.documents;

import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		instance = new DebtDocEx();
	}

	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		return new DebtDocListEx(whereStr, order, LoadDelivery);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		if( !(doc.getData() instanceof PaymentEx) ) {
			super.setView(adapter, view, doc);
			return;
		}
					
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		
		PaymentEx p = (PaymentEx)doc.getData();
		if (doc.getDate() == null)
			tv.setText(R.string.doc_error);
		else
			tv.setText(Util.simpleDateFormat.format(doc.getDate()));
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		String text;
		text = Util.IntToScaleWStr(p.sum, Consts.SUM_SCALE, 2, false);
		text += "\n" + Util.IntToScaleWStr(p.sum2, Consts.SUM_SCALE, 2, false);
		tv.setText(text);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText("баланс\nпросрочено");		
	}
}

class DebtDocListEx extends DebtDocList {
	public DebtDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, false);
	}
}
