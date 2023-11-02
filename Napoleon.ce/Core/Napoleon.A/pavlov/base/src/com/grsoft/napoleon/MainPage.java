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
import android.content.Intent;
import android.os.Bundle;
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
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;

public class MainPage extends Activity
{
	private static final int SERT = 8;
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
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
		OrderImpl order = CreateOrder.currentOrder();
		OrderEx o = (OrderEx)order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

        ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerWithKey(config, "Организация", firms, spFirma, o.suplCode);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);

		boolean canChangeCost = false;
		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 1)
				canChangeCost = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		spPrices.setEnabled(canChangeCost);
		
		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);

		if( (o.params & SERT) != 0 )
			((CheckBox)findViewById(R.id.cbSert)).setChecked(true);
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
		if( spAddress != null ) {
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

        updateDisplayDelay();
	}

	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				CreateOrder.currentOrder().getData().delay);
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
			tvCounter.setText(Integer.toString(CreateOrder.currentOrder().getData().delay));
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
					CreateOrder.currentOrder().getData().delay = Integer.parseInt(tvCounter.getText().toString());
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
	
	public void update(OrderImpl ord) {
		OrderEx o = (OrderEx) ord.getData();
		o.date = timeHandler.adjustTime(dateHandler.getDate());
		
		if (o.created == null)
			o.created = new Date();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		int costType = spPrices.getSelectedItemPosition();

		if( suppl >= 0 ) {
			o.supplyer = suppl;
			o.suplCode = ((KeyValue)spFirma.getSelectedItem()).key.toString();
		}
		if( costType >= 0 )
			o.sumType = costType;
		
		CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
		if( cash.isChecked() ) o.params |= ParamState.ofCash;
		else o.params &= (~ParamState.ofCash);

		if( ((CheckBox)findViewById(R.id.cbSert)).isChecked() ) o.params |= SERT;
		else o.params &= (~SERT);
		
		Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
		if( spAddress != null ) {
			KeyValue sel = (KeyValue) spAddress.getSelectedItem();
			if( sel != null )
				o.adrCode = sel.key.toString();
		}

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		o.remark = remark.getText().toString();
	}
}
