/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.BaseActivity;

public class ReturnProperties extends BaseActivity
{
	private ReturnImpl doc = new ReturnImpl();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	
	private boolean editMode = false;
	
	DateHandler dateHandler;
	
	class OrderData {
		public Date created;
		
		Date date;
		int sum;
		
		public OrderData(OrderImpl doc) {
			OrderEx oe = (OrderEx) doc.getData();
			created = oe.created;
			date = oe.date;
			sum = doc.sum();
		}
		
		@Override
		public String toString() { 
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
			String ttt = sd.format(date);
			ttt += " " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			return ttt;
		}
	}
	ArrayList<OrderData> orders = new ArrayList<OrderData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.returnproperties);
		init();
	}
	
	public static void open(Context context, ReturnImpl order, boolean editOldOrder) {
		Intent i = new Intent(context, ReturnProperties.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		doc.read(orderRowId);
		ReturnEx o = (ReturnEx)doc.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		int selected = -1;
		DocList docs = OrderDoc.instance().docList(o.id);
		for(Document<?> doc : docs) {
			OrderEx od = (OrderEx)doc.getData();			
			if( od.created.compareTo(o.shedule) == 0 )
				selected = orders.size();
			orders.add(new OrderData((OrderImpl)doc));
		}
		
		Spinner s = (Spinner)findViewById(R.id.spOrders);
		ArrayAdapter<OrderData> adapter = new ArrayAdapter<OrderData>(this, R.layout.simple_spinner_layout, orders);
		s.setAdapter(adapter);
		if( selected >= 0 && selected < s.getCount())
			s.setSelection(selected);
		
		docs.close();

		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!doc.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}
	
	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyOrder();			
			finish();
		}
	}
	
	private void deleteEmptyOrder() {
		if(!editMode) {
			if( doc.getData().items == null || doc.getData().items.size() == 0 )
				doc.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			ReturnEx o = (ReturnEx) doc.getData();
			o.date = dateHandler.getDate();
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner s = (Spinner)findViewById(R.id.spOrders);
			OrderData od = (OrderData) s.getSelectedItem();
			if( od != null )
				o.shedule = od.created;

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			doc.write();
			
			if(!editMode)
				Warehouse.open(ReturnProperties.this, doc, false);
			
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyOrder();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}
