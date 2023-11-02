package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class VisitEditorEx extends VisitEdit {
	
	final static String ORDER_ID = "OrderDocID";
	long orderRowId = ExtrasConst.INVALID_ID;
	
	public static void openBeforeOrder(Context ctx, long orderRID) {
		OrderImpl oi = new OrderImpl();
		Order o = oi.getData();
		oi.read(orderRID);
		
		VisitImpl vi = new VisitImpl();
		Visit v = vi.getData();
		v.latitude = o.latitude;
		v.longitude = o.longitude;
		v.id = o.id;
		v.date = Util.getDateTime();
		v.created = Util.getDateTime();
		vi.write();
		VisitDoc.instance().refreshDocSum(v.id);
		
		Intent i = new Intent(ctx, VisitEditorEx.class);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, vi.getRowid());
		i.putExtra(ORDER_ID, orderRID);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ORDER_ID, orderRowId);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && orderRowId != ExtrasConst.INVALID_ID ) { 
			OrderImpl oi = new OrderImpl();
			oi.read(orderRowId);
			oi.editProperties(this, false);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		orderRowId = b.getLong(ORDER_ID, ExtrasConst.INVALID_ID);

		super.onCreate(savedInstanceState);
	}
}
