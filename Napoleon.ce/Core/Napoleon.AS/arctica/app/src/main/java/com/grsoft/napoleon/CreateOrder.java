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
import java.util.List;

import com.grsoft.dataobjects.Agreement;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.KAgents;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	OrgEx oe;
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private static final String BY_PHONE = "by_phone";
	
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}

	public static void createByPhone(Context context, OrderImpl order) { 
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, false);
		i.putExtra(BY_PHONE, true);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
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
	
	int seli = 0;
	void loadAgreements(String firma, final String sel) {
		seli = 0;
		final List<Agreement> list = new ArrayList<Agreement>();
		for(Agreement a : oe.agree) {
			if(a.firm.equals(firma)) {
				if(a.id.equals(sel))
					seli = list.size(); 
				list.add(a);
			}
		}
		
		DataTraveler.travel(Agreement.class, new DataTraveler.Travel<Agreement>(true){

			@Override
			public boolean travel(DataTraveler<Agreement> item) {
				if(oe.haveSegment(item.data.idSeg)) {
					if(item.data.id.equals(sel))
						seli = list.size(); 
					list.add(item.data);
				}
				return true;
			}
			
		}, "firm='" + firma + "'");
		
		Spinner sp = (Spinner)findViewById(R.id.spAgreement);
		ArrayAdapter<Agreement> aa = new ArrayAdapter<Agreement>(this, R.layout.simple_spinner_layout, list);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( seli < list.size())
			sp.setSelection(seli);
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		final OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		oe = (OrgEx) oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);

		if( !editMode ) {
			boolean byPhone = getIntent().getBooleanExtra(BY_PHONE, false);
			initOrder(o, (OrgEx)oi.getData());
			o.byPhone = byPhone ? 1 : 0;
		}
		
		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);
		String firma = firms.size() > 0 ? firms.get(0).toString() : "";
		if(o.supplyer > 0 && o.supplyer < firms.size())
			firma = firms.get(o.supplyer).toString();
		
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				loadAgreements(firms.get(arg2).toString(), "");
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		Spinner spKA = (Spinner)findViewById(R.id.spKAgent);
		DialogHelper.loadSpinnerFromDataObject(spKA, KAgents.class, new DialogHelper.Selected<KAgents>() {
			@Override public boolean isSelected(KAgents object) { return object.id.equals(o.kagent); }
		}, false, "", "ido='" + oe.ido + "'");
		
		loadAgreements(firma, o.agreement);
		
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", new ArrayList<CharSequence>(), spPrices, o.sumType);
		
		EditText ed = (EditText)findViewById(R.id.edPkoCost);
		ed.setText(Util.IntToScaleStr(o.pkoCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

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

		CheckBox cb = (CheckBox)findViewById(R.id.cbCreateOrderCash);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				findViewById(R.id.spFirma).setEnabled(!isChecked);
			}
		});
		
		((CheckBox)findViewById(R.id.cbByPhone)).setChecked(o.byPhone > 0);
		
		if( (o.params & ParamState.ofCash) != 0 )
			cb.setChecked(true);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDelay();
		refreshDate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			order.getData().date = newDate;
			refreshDate();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(Order o, OrgEx org) {
		o.sumType = org.costype;
		if( org.delay > 0 )
			o.delay = org.delay;
	}
	
	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				order.getData().delay);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
//			case DIALOG_DATE_PICKER_ID:
//				return dateHandler.createDialog();
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
			Spinner sp = (Spinner) findViewById(R.id.spAgreement);
			Agreement asel = (Agreement) sp.getSelectedItem();
			if(asel == null) {
				Toast.makeText(CreateOrder.this,"Не выбрано соглашение", Toast.LENGTH_LONG).show();
				return;
			}
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(o.date);
//			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();

			o.agreement = asel.id;

			if( suppl >= 0 )
				o.supplyer = suppl;

			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();
			if( costType >= 0 )
				o.sumType = costType;
			
			String val = ((EditText)findViewById(R.id.edPkoCost)).getText().toString();
			o.pkoCost = (val.length() > 0) ? Util.StrToScale(val, Consts.SUM_SCALE) : 0;
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			o.byPhone =((CheckBox)findViewById(R.id.cbByPhone)).isChecked() ? 1 : 0;
			
			if( Features.DELIVERY_ADDRESS ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					KeyValue sel = (KeyValue) spAddress.getSelectedItem();
					if( sel != null )
						o.adrCode = sel.key.toString();
				}
			}
			
			KAgents ksel = (KAgents) ((Spinner)findViewById(R.id.spKAgent)).getSelectedItem();
			if(ksel != null)
				o.kagent = ksel.id;
			
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
