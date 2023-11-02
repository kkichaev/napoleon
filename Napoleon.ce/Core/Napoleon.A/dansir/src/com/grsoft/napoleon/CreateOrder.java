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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
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
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<KeyValue> priceType = new ArrayList<KeyValue>();
	
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
		OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		if( !editMode ) 
			initOrder(o, (OrgEx) oi.getData());

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", priceType, spPrices, o.priceType);

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		if( Features.DELIVERY_ADDRESS ) {
			View v = findViewById(R.id.ftrAddress);
			if( v != null ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					v.setVisibility(View.VISIBLE);
					ArrayList<KeyValue> addresses = new ArrayList<KeyValue>();
					int selected = -1;
					for(OrgAddress addr : oi.getData().orgAddress) {
						KeyValue kv = new KeyValue(addr.id, addr.name);
						if( kv.key.toString().equals(o.adrCode))
							selected = addresses.size();
						addresses.add(kv);
					}
					ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
					spAddress.setAdapter(aa);
					if( selected >= 0 && selected < spAddress.getCount())
						spAddress.setSelection(selected);
				}
			}
		}
		
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

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		o.priceType = org.priceType;

		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.date = c.getTime();
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
			
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( suppl >= 0 )
				o.supplyer = suppl;
			if( costType >= 0 ) {
				o.sumType = costType;
				o.priceType = priceType.get(costType).key.toString();
			}
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if( Features.DELIVERY_ADDRESS ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					KeyValue sel = (KeyValue) spAddress.getSelectedItem();
					if( sel != null )
						o.adrCode = sel.key.toString();
				}
			}
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
