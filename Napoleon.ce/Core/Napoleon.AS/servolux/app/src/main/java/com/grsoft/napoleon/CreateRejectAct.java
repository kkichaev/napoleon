package com.grsoft.napoleon;

import java.util.Calendar;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;

public class CreateRejectAct extends CreateReturnRequest {

	public static void open(Context context, OrderImplBase<? extends Order> doc, boolean editOldOrder) {
		Intent i = new Intent(context, CreateRejectAct.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override protected OrderImplBase<? extends Order> createDoc() { return new RejectActImpl(); }
	@Override protected int getLayoutID() { return R.layout.create_reject_act; }
	
	@Override
	protected void refreshDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(doc.getDate());
		int m = c.get(Calendar.MONTH);
		Spinner sp = (Spinner)findViewById(R.id.spMonth);
		sp.setSelection(m);
	}
	
	@Override protected void openPrice() { 
		doc.open(this);
		RejectActPrice.open(this, doc); 
	}
	@Override protected void initDateView() { }
	
	@Override
	protected boolean saveChanges() {
		Calendar c = Calendar.getInstance();
		c.setTime(doc.getDate());
		Spinner sp = (Spinner)findViewById(R.id.spMonth);
		c.set(Calendar.MONTH, sp.getSelectedItemPosition());
		doc.getData().date = c.getTime();
		
		return super.saveChanges();
	}
}
