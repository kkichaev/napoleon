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

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateOrder extends TabActivity
{
	private static final String MAIN_PAGE = "main";
	private static final String OTHER_PAGE = "other";
	
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	private boolean editMode = false;
	
	private static CreateOrder instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderprops);
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
	
	public static OrderImpl currentOrder() {
		return (instance == null) ? null : instance.order;
	}

	private void init() {
		instance = this;
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx o = (OrderEx)order.getData();
		
		if( !editMode ) 
			initOrder(o);


		initTabs();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}

	private void initTabs() {
		// init tabs		
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
		
	    intent = new Intent().setClass(this, MainPage.class);
	    spec = tabHost.newTabSpec(MAIN_PAGE).setIndicator("Основная").setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, AddPage.class);
	    spec = tabHost.newTabSpec(OTHER_PAGE).setIndicator("Дополнительно").setContent(intent);
	    tabHost.addTab(spec);
	}
	
	String getFirstValue(ConfigImpl ci, String key) {
		Config c = ci.getData();
		c.key = key;
		if(ci.read()) {
			ArrayList<CharSequence> values = new ArrayList<CharSequence>();
			DialogHelper.makeList(c.value, values);
			if(values.size() > 0)
				return values.get(0).toString();
		}
		return "";
	}

	String getFirstKeyValue(ConfigImpl ci, String key) {
		Config c = ci.getData();
		c.key = key;
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, values, "");
			if(values.size() > 0)
				return values.get(0).key.toString();
		}
		return "";
	}
	
	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o) {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = o.id;
		oi.read();
		
		o.sumType = oe.costype;
		o.remark = oe.comment;
		o.dlvFrom = oe.dlvFrom;
		o.dlvTill = oe.dlvTill;
		
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if( c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
			c.add(Calendar.DAY_OF_MONTH, 1);
		
		o.date = c.getTime();

		ConfigImpl config = new ConfigImpl();
		o.dlvMethod = getFirstValue(config, "МетодДоставки");
		o.ctrlType = getFirstValue(config, "ТипУчета");
		o.payMethod = getFirstValue(config, "МетодОплаты");
		o.firmCode = getFirstKeyValue(config, "Организация");
		o.dlvDir = getFirstKeyValue(config, "НаправлениеОтгрузки");
		
		OrderSettings os = OrderSettings.load(this);
		if(os.dlvMethod.length() > 0)
			o.dlvMethod = os.dlvMethod;
		if(os.ctrlType.length() > 0)
			o.ctrlType = os.ctrlType;
		if( os.cash )
			o.params |= ParamState.ofCash;
	}
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	

	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			if(!editMode) {
				if( order.getData().items == null || order.getData().items.size() == 0 )
					order.delete();
			}
				
			finish();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
						
			MainPage mp = (MainPage)getLocalActivityManager().getActivity(MAIN_PAGE);
			if( mp != null )
				mp.update(order);
			
			AddPage ap = (AddPage)getLocalActivityManager().getActivity(OTHER_PAGE);
			if( ap != null )
				ap.update(order);
			
			order.write();
			
			super.onClick(v);
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
	}

	private void deleteEmptyOrder() {
		if(!editMode) {
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	public static void checkEmptyOrder() {
		if( instance != null ) {
			instance.deleteEmptyOrder();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			deleteEmptyOrder();
			finish();
		}
		
		return true;
	}
}
