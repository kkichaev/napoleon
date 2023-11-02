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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Close;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	protected OrderImplBase<? extends Order> order;
	private OrgImpl org = new OrgImpl();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_PAY_DATE = 1;
	
	private boolean editMode = false;
	
	private ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
	private ArrayList<KeyValue> address = new ArrayList<KeyValue>();
	private ArrayList<DogData> dogovors = new ArrayList<DogData>();

	class DogData {
		public String number;
		public String name;
		public String cost;
		
		public DogData(OrgDogovor dog) {
			name = dog.name;
			number = dog.number;
			cost = dog.ctype;
		}
		
		@Override public String toString() { return name; }
	}
	
	DateHandler dateHandler;
	DateHandler payHandler;
	
	int getContentViewID() { return R.layout.createorder; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		order = createDocument();
		setContentView(getContentViewID());
		init();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		order.close();
		org.close();
	}
	
	protected OrderImplBase<? extends Order> createDocument() { return new OrderImpl(); }
	
	public static void open(Context context, OrderImplBase<? extends Order> order) { 
		open(context, order, true); 
	}
	
	public static void open(Context context, OrderImplBase<? extends Order> order, boolean editOldOrder) {
		if( !editOldOrder && context instanceof DocumentsEx ) {
			((DocumentsEx)context).askForOrderCreation(order.getRowid());
			return;
		}
		
		forceOpen(context, order, editOldOrder);
	}

	public static void forceOpen(Context context, OrderImplBase<? extends Order> order, boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	protected void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx o = (OrderEx)order.getData();

		org.getData().id = o.id;
		org.read();
		org.close();
		
		if( !editMode ) 
			initOrder(o);

        ((TextView) findViewById(R.id.tvOrgName)).setText(org.getData().name);

        ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onFirmChanged(firms.get(arg2).key.toString());
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		DialogHelper.loadSpinnerWithKey(config, "Организация", firms, spFirma, o.suplCode);
		
		loadAddress((Spinner)findViewById(R.id.spUnit), address, o.dlvCode);

		config.close();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCash)).setChecked(true);
		
		if( (o.params & OrderEx.OF_DELIVERY) != 0 )
			((CheckBox)findViewById(R.id.cbDelivery)).setChecked(true);
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, o.delay);
		payHandler = new DateHandler((TextView)findViewById(R.id.tvPayDate), c.getTime(), DIALOG_PAY_DATE);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        
        ((ScrollView)findViewById(R.id.scrollview)).smoothScrollTo(0,0);
	}

	protected void onFirmChanged(String firmId) {
		OrgEx oe = (OrgEx)org.getData();
		for(Close cls : oe.closed) {
			if( firmId.equals(cls.firm)) {
				MessageBox.show(this, "Предупреждение", "Контрагент закрыт по фирме!");
				break;
			}
		}
		
		int sel = -1;
		String id = ((OrderEx)order.getData()).dogovor;
		dogovors.clear();
		for(OrgDogovor d : oe.dogovors) {
			if( d.firm.equals(firmId)) {
				DogData dd = new DogData(d);
				if( dd.number.equals(id))
					sel = dogovors.size();
				dogovors.add(dd);
			}
		}
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		ArrayAdapter<DogData> aa = new ArrayAdapter<DogData>(s.getContext(), R.layout.simple_spinner_layout, dogovors);
		s.setAdapter(aa);
		if( sel >= 0 && sel < s.getCount())
			s.setSelection(sel);		
	}

	private void loadAddress(Spinner s, ArrayList<KeyValue> values, String dlvCode) {
		int sel = -1;
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String table = DataObjectInfo.getInstance().getTableName(Org.class);
		String sql = "select id, name from " + table + " where ido='" + ((OrgEx)org.getData()).ido + "'";
		try {
			Cursor c = db.rawQuery(sql, null);
			while( c.moveToNext() ) {
				String key = c.getString(0);
				if( dlvCode.equals(key))
					sel = values.size();
				values.add(new KeyValue(key, c.getString(1)));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(s.getContext(), R.layout.simple_spinner_layout, values);
		s.setAdapter(aa);
		if( sel >= 0 && sel < s.getCount())
			s.setSelection(sel);		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	protected void initOrder(OrderEx o) {
		o.dlvCode = o.id;
		o.delay = 5;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
			case DIALOG_PAY_DATE:
				return payHandler.createDialog();
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
	
	protected void updateOrder(OrderEx o) {
		o.date = dateHandler.getDate();
		
		if (o.created == null)
			o.created = new Date();
		
		int suppl = ((Spinner) findViewById(R.id.spFirma)).getSelectedItemPosition();
		o.supplyer = suppl;
		
		if(firms.size() > suppl)
			o.suplCode = firms.get(suppl).key.toString();
		else
			o.suplCode = "";

		int dog = ((Spinner) findViewById(R.id.spDogovor)).getSelectedItemPosition();
		if( dog >= 0 ) {
			DogData dd = dogovors.get(dog);
			o.sumType = Features.COST_MANAGER.getCostIndex(dd.cost);
			o.dogovor = dd.number;
		}
		
		int adr = ((Spinner) findViewById(R.id.spUnit)).getSelectedItemPosition();
		if( adr >= 0 )
			o.dlvCode = address.get(adr).key.toString();
		
		long delay = payHandler.getDate().getTime() / (24l * 3600 * 1000);
		delay -= o.date.getTime() / (24l * 3600 * 1000);
		o.delay = (int)delay;

		if( ((CheckBox)findViewById(R.id.cbCash)).isChecked() ) o.params |= ParamState.ofCash;
		else o.params &= (~ParamState.ofCash);

		if( ((CheckBox)findViewById(R.id.cbDelivery)).isChecked() ) o.params |= OrderEx.OF_DELIVERY;
		else o.params &= (~OrderEx.OF_DELIVERY);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		o.remark = remark.getText().toString();
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			OrderEx o = (OrderEx) order.getData();
			updateOrder(o);
			order.write();
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
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
