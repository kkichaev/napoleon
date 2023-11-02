/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;

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
		
		OrgImpl orgImpl = new OrgImpl();
		OrgEx org = (OrgEx) orgImpl.getData();
		org.id = o.id;
		orgImpl.read();
		orgImpl.close();
		
		if( !editMode ) 
			initOrder(o, org);

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
	
	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 * @param org 
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		o.date = c.getTime();
		o.gprm = o.id;
		o.consignee = o.id;
		o.payer = o.id;
		o.regCode = org.region;
		
		if(org != null && org.dogovors != null && org.dogovors.size() > 0){
			OrgDogovor d = org.dogovors.get(0);
			o.sumType = d.costype;
			o.dogCode = d.id;
		}

		DbReader r = new DbReader();
		Agent a = new Agent();
		String wh = "login='" + ConfigManager.getConfig().login + "' and password='" + 
				ConfigManager.getConfig().passw + "'";
		boolean bdo = r.select(a, DataObjectInfo.getInstance().getTableName(a.getClass()), wh);
		if( bdo )
			o.executiveManager = a.id;			
		r.close();
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
			if( mp != null ) {
				if( !mp.update(order) )
					return;
			}
			
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
	
	public static void checkOrder() {
		if( instance != null )
			instance.deleteEmptyOrder();
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
