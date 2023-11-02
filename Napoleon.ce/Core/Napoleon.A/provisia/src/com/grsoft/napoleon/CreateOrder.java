/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;

public class CreateOrder extends Activity
{
	//private OrgImpl org = new OrgImpl();
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
	DateHandler dateHandler;
	TimeHandler timeHandler;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}
	
	public static void open(Context context, OrderImpl order) { 
		open(context, order, true); 
	}
	
	public static void open(Context context, OrderImpl order, boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	private void init() {
		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		
		Button btnOK = (Button) findViewById(R.id.btnOK);
				
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		Calendar calendar = Calendar.getInstance();
		
		order.read(orderRowId);
		OrderEx o = (OrderEx)order.getData();

		ConfigImpl config = new ConfigImpl();
		DialogHelper.loadSpinnerWithKey(config, "Организация", new ArrayList<KeyValue>(), 
				(Spinner) findViewById(R.id.spFirma), o.supplCode);
		
		DialogHelper.loadSpinnerFromConfig(config, "ФормаОплаты", new ArrayList<CharSequence>(), 
				(Spinner) findViewById(R.id.spPrices), o.sumType);
						
		btnOK.setEnabled(!order.isExported());
		calendar.setTime(o.date);

		EditText ed = (EditText)findViewById(R.id.edDiscount);
		ed.setText(Util.IntToScaleStr(o.discount, Consts.DISCOUNT_SCALE));
		ed.selectAll();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		if( (o.params & OrderEx.ofNetCost) != 0 ) {
			CheckBox cash = (CheckBox)findViewById(R.id.cbNetCost);
			cash.setChecked(true);
		}
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);

		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		dateHandler = new DateHandler(tv, o.date, DIALOG_DATE_PICKER_ID);
		
		tv = (TextView)findViewById(R.id.tvTime);
		tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		timeHandler = new TimeHandler(tv, o.date, DIALOG_TIME_PICKER_ID); 

		btnCancel.setOnClickListener(new CancelClickListener());
		
		btnOK.setOnClickListener(new OKClickListener());
		
		OrgImpl org = new OrgImpl();
		org.getData().id = order.getId();
		org.read();
		tvOrgName.setText(org.getData().name);
		
		int id = (o.sendBefore > 0) ? R.id.rbBefore : R.id.rbAfter;
		RadioButton rb = (RadioButton)findViewById(id);
		rb.setChecked(true);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
				
			case DIALOG_TIME_PICKER_ID:
				return timeHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		order.close();
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
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			OrderEx o = (OrderEx)order.getData();
			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
			
			Spinner sp = (Spinner) findViewById(R.id.spFirma);
			KeyValue val = (KeyValue) sp.getSelectedItem();
			if( val != null ) {
				o.supplCode = val.key.toString();
				o.supplyer = sp.getSelectedItemPosition();
			}
			
			sp = (Spinner) findViewById(R.id.spPrices);
			o.sumType = sp.getSelectedItemPosition();
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			CheckBox cash = (CheckBox)findViewById(R.id.cbNetCost);
			
			if( cash.isChecked() ) o.params |= OrderEx.ofNetCost;
			else o.params &= (~OrderEx.ofNetCost);
			o.remark = remark.getText().toString();
			
			EditText ed = (EditText)findViewById(R.id.edDiscount);
			o.discount = Util.StrToScale(ed.getText().toString(), Consts.DISCOUNT_SCALE);

			RadioButton rb = (RadioButton)findViewById(R.id.rbBefore);
			o.sendBefore = (rb.isChecked()) ? 1 : 0;
			
			order.write();
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		deleteEmptyOrder();
		super.onBackPressed();
	}
}
