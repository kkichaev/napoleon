package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;

public class ReturnProperties extends CreateOrder {

	public static void open(Context context, OrderImplBase<? extends Order> order, boolean editOldOrder) {
		Intent i = new Intent(context, ReturnProperties.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	@Override int getContentViewID() { return R.layout.createreturn; }
	
	@Override
	protected void init() {
		super.init();
		
		EditText ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(((OrderEx)order.getData()).retNum);
	}
	
	@Override
	protected void updateOrder(OrderEx o) {
		super.updateOrder(o);
		
		EditText ed = (EditText)findViewById(R.id.edNumber);
		o.retNum = ed.getText().toString();
	}

	protected OrderImplBase<? extends Order> createDocument() { return new ReturnImplEx(); }
}
