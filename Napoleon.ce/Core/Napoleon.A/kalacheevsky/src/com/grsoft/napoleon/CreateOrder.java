/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
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
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		order.close();
		OrderEx o = (OrderEx) order.getData();
		
		final OrgImpl oi = new OrgImpl();
		Org oe = oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);

		if( !editMode ) 
			initOrder(o, oe);

        ConfigImpl config = new ConfigImpl();

//        Spinner spFirm = (Spinner)findViewById(R.id.spFirma);
//        DialogHelper.loadSpinnerWithKey(config, "Организация", new ArrayList<KeyValue>(), spFirm, 
//        		((OrderEx)o).firmCode);
//        
//        spDog = (Spinner)findViewById(R.id.spDogovors);
//        
//        spFirm.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				Adapter a = arg0.getAdapter();
//				
//				if (a != null){
//					KeyValue kv = (KeyValue) a.getItem(arg2);
//					
//					if (kv != null){
//						Adapter dogAdapter = spDog.getAdapter();
//						
//						if (dogAdapter != null){
//							DogovorImpl dogovorImpl = new DogovorImpl();
//							dogovorImpl.getData().id = extractOrgid((OrgEx) oi.getData());
//							
//							for(int i = 0; i < dogAdapter.getCount(); i++){
//								KeyValue dkv = (KeyValue) dogAdapter.getItem(i);
//								
//								if (dkv != null){
//									dogovorImpl.getData().num = (String) dkv.key;
//									
//									if (dogovorImpl.read() 
//											&& kv.key.equals(dogovorImpl.getData().firm)){
//										spDog.setSelection(i);
//										break;
//									}
//								}
//							}
//							
//							dogovorImpl.close();
//						}
//					}
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//			}
//		});
        
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", new ArrayList<CharSequence>(), spPrices, o.sumType);
		spPrices.setEnabled(false);
		
		Spinner spStore = (Spinner) findViewById(R.id.spStore);
		DialogHelper.loadSpinnerFromConfig(config, "Склад", new ArrayList<CharSequence>(), spStore, 
				o.storecode);
		
		
		createDogovorSpinner(this, (OrderEx)o, (OrgEx)oe);
		
		TableRow trPrices = (TableRow) findViewById(R.id.trPrices);
		trPrices.setVisibility(View.GONE);
//		config.getData().key = "МожноИзменятьЦену";
//		try {
//			if (config.read() && Integer.parseInt(config.getData().value) == 0)
//				spPrices.setEnabled(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		config.close();
		
		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDelay();
	}

	protected void setSelectedCType(int ctype) {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		if (ctype >= 0 && ctype < spPrices.getAdapter().getCount())
			spPrices.setSelection(ctype);
	}

	/***
	 * Возвращает тип цены выбранного договора,
	 * или 0, цена - по умолчанию
	 */
	public void createDogovorSpinner(Activity activity, OrderEx o, final OrgEx oe) {
		int selected = -1;
		ArrayList<KeyValue> v = new ArrayList<KeyValue>();
		int index = 0;
		
		String orgid = extractOrgid(oe);
		Cursor<Dogovor> dogCursor = new Cursor<Dogovor>(new DogovorImpl(), 
				String.format("id='%s'", orgid));
		
		String docCode = o.dogCode.length() > 0 ? o.dogCode : oe.dogCode;
		while(dogCursor.moveNext()){
			Dogovor d = dogCursor.current().getData();
			
			String name = String.format("(%s)%s", d.num, d.name);
			if( d.isBlocked() )
				name = "БЛОКИРОВАН " + name;
			KeyValue kv = new KeyValue(d.num, name);
			
			if(selected < 0 && docCode.length() > 0 && kv.key.equals(docCode))
				selected = index;
			
			index++;
			v.add(kv);
		}
		
		dogCursor.close();
		
		if (o.dogCode.length() == 0 && oe.dogCode.length() == 0 && v.size() > 0)
			selected = 0;
		
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(activity, R.layout.simple_spinner_layout, v);
		Spinner spDog = (Spinner)findViewById(R.id.spDogovors);
		spDog.setAdapter(aa);
		spDog.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyValue kv = (KeyValue)arg0.getAdapter().getItem(arg2);
				
				if (kv != null){
					String dognum = (String) kv.key;
					
					if (dognum != null){
						DogovorImpl dogovorImpl = new DogovorImpl();
						Dogovor dogovor = dogovorImpl.getData();
						dogovor.id = extractOrgid(oe);
						dogovor.num = dognum;
						if (dogovorImpl.read())
							setSelectedCType(dogovor.ctype);
						dogovorImpl.close();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		if( selected >= 0 && selected < spDog.getCount())
			spDog.setSelection(selected);
	}

	protected String extractOrgid(final OrgEx oe) {
		String result = "";
		if (oe != null && oe.id != null && oe.id.trim().length() > 0){
			int tabPos = oe.id.indexOf('\t');
			
			if (tabPos == -1)
				tabPos = oe.id.length();
			result = oe.id.substring(0, tabPos);
		}
		
		return result;
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(Order o, Org org) {
		o.sumType = org.costype;
	}
	
	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				order.getData().delay);
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
	
	class DelayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Отсрочка");
			View dialogView = View.inflate(v.getContext(), R.layout.counter, null);
			
			builder.setView(dialogView);
			final AlertDialog dialog = builder.create();
			
			Button btnCounterUp = (Button) dialogView.findViewById(R.id.btnCounterUp);
			Button btnCounterDown = (Button) dialogView.findViewById(R.id.btnCounterDown);
			Button btnCounterOK = (Button) dialogView.findViewById(R.id.btnCounterOk);
			Button btnCounterCancel = (Button) dialogView.findViewById(R.id.btnCounterCancel);
			final  TextView  tvCounter = (TextView) dialogView.findViewById(R.id.edCounter);
			tvCounter.setText(Integer.toString(order.getData().delay));
			tvCounter.setFocusable(false);
			
			btnCounterUp.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					++val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterDown.setOnClickListener(new OnClickListenerToNotify() {
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					
					if (val > 0)
						--val;
					
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterOK.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					order.getData().delay = Integer.parseInt(tvCounter.getText().toString());
					updateDisplayDelay();
					dialog.hide();
				}
			});
			
			btnCounterCancel.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					dialog.hide();
				}
			});
		
			dialog.show();
		}
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
			
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();
			
			if (editMode && (order.getSumType() != costType && costType >= 0))
				askToApplyNewSumType(v.getContext(), costType);
			else 
				okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			Order o = order.getData();
			o.date = dateHandler.getDate();
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();
			Spinner spStore = (Spinner) findViewById(R.id.spStore);
			int store = spStore.getSelectedItemPosition();
			
			if (store > 0)
				((OrderEx)o).storecode = store;
			if( suppl >= 0 )
				o.supplyer = suppl;
			if( costType >= 0 )
				o.sumType = costType;
			
			KeyValue sel = (KeyValue)((Spinner)findViewById(R.id.spDogovors)).getSelectedItem();
			if( sel != null )
				((OrderEx)o).dogCode = sel.key.toString();
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if (updateSumType)
				order.updateItemsCost(o.sumType);
			else
				order.write();
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
		
		private void askToApplyNewSumType(Context context, final int newSumType){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Внимание");
			builder.setMessage("Тип цены был изменен, пересчитать заказ?");

			builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(true);
				}
			});
			
			builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(false);
				}
			});
			
			builder.create().show();
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
