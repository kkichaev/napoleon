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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.Agreements;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImplBase<?> order = null;
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false, initing = true;
	
	HashMap<String, FirmEx> firms = new HashMap<String, FirmEx>();
	
	int selected = -1; 
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	private Button btnDlvType;
	
	Spinner spStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		
		order = (OrderImplBase<?>) DocType.getCurDoc().create();
		
		btnDlvType = (Button) findViewById(R.id.btnDlvType);
		spStatus = (Spinner) findViewById(R.id.spStatus);
	}
	
	public static void open(Context context, OrderImpl order) { 
		open(context, order, true); 
	}
	
	public static void open(Context context, Document<?> order, boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	void refreshFirm() {
		IOrder o = (IOrder)order.getData();
		Firm f = firms.get(o.getFirmCode());
		String name = "?";
		
		if( f != null )
			name = f.name;
		
		TextView  tv = (TextView)findViewById(R.id.tvFirm);
		tv.setText(name);
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId, false);
		IOrder o = (IOrder)order.getData();
		
		OrgImpl oi = new OrgImpl();
		final OrgEx org = (OrgEx) oi.getData();		
		org.id = order.getId();
		oi.read();
		oi.close();

		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) 
			initOrder(o, org);

		EditText ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(o.getDocNumber());
		
		ConfigImpl config = new ConfigImpl();
		
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<FirmEx>() {
			@Override
			public boolean travel(DataTraveler<FirmEx> item) {
				firms.put(item.data.id, item.data);
				item.data = new FirmEx();
				return true;
			}}, null);
		
		final Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", new ArrayList<KeyValue>(), spPrices, o.getPriceType());

		selected = 0;
		final List<Agreements> va = new ArrayList<Agreements>();
		DataTraveler.travel(Agreements.class, new DataTraveler.Travel<Agreements>() {

			@Override
			public boolean travel(DataTraveler<Agreements> item) {
				IOrder oe = (IOrder)order.getData();
				if( !editMode ) {
					if( oe.getAgreement().length() == 0 || org.ido.equals(item.data.idOrg) ) {
						oe.setAgreement(item.data.id);
						selected = va.size();
						oe.setFirmCode(item.data.idFirm);
						
						if(firms.containsKey(item.data.idFirm))
							initDocNumber(item.data.idFirm);

						refreshFirm();
						if( item.data.cost < spPrices.getAdapter().getCount() )
							spPrices.setSelection(item.data.cost);
					}
				} else if( oe.getAgreement().equals(item.data.id) )
					selected = va.size();
				va.add(item.data);
				item.data = new Agreements();
				return true;
			}
		}, "common=1 or idOrg = '" + org.ido + "'", "common,name");
		
		BaseAdapter adapter = new ArrayAdapter<Agreements>(this,  R.layout.simple_spinner_layout, va);
		Spinner spAgr = (Spinner)findViewById(R.id.spAgr);
		spAgr.setAdapter(adapter);
		if( selected < va.size() )
			spAgr.setSelection(selected);
		
		spAgr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( initing )
					initing = false;
				else
				{
					Agreements agr = (Agreements) arg0.getAdapter().getItem(arg2);
					if( agr != null ) {
						IOrder oe = (IOrder)order.getData();
						oe.setAgreement(agr.id);
						oe.setFirmCode(agr.idFirm);
						initDocNumber(agr.idFirm);
						refreshFirm();
						if( agr.cost < spPrices.getAdapter().getCount() )
							spPrices.setSelection(agr.cost);
					}
				}
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		

		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, o.getWhCode());

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
						if( kv.key.toString().equals(o.getAdrCode()))
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
		remark.setText(o.getRemark());

		if( (o.getParams() & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.getDate(), DIALOG_TIME_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable() && o.getDlvType() > 0);
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDelay();
		refreshDate();
		
		btnDlvType.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) {
			order.write();
			DeliveryType.open(v.getContext(), order.getRowid());} 
		});
	
		if( editMode )
			refreshFirm();
		
		btnDlvType.setText(getResources().getStringArray(R.array.delivery_types)[o.getDlvType()]);
		
		DialogHelper.loadSpinnerFromConfig(config, "Статусы", new ArrayList<CharSequence>(), spStatus, o.getStatus());
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
	
	@Override
	protected void onResume() {
		super.onResume();
		init();
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(IOrder o, OrgEx org) {
		o.setPriceType(org.prcType);
	}
	
	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				((IOrder)order.getData()).getDelay());
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
			tvCounter.setText(Integer.toString(((IOrder)order.getData()).getDelay()));
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
					((IOrder)order.getData()).setDelay(Integer.parseInt(tvCounter.getText().toString()));
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
			if( ((IOrder)order.getData()).isEmty())
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
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			
			IOrder o = (IOrder) order.getData();
			o.setDate(timeHandler.adjustTime(o.getDate()));
			o.setSumType(spPrices.getSelectedItemPosition());
			o.setPriceType(((KeyValue)spPrices.getSelectedItem()).key.toString());
			o.setStatus((String)spStatus.getSelectedItem());
			
			KeyValue whSel = (KeyValue)((Spinner) findViewById(R.id.spWh)).getSelectedItem();
			if( whSel != null )
				o.setWhCode(whSel.key.toString());
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.setParams(o.getParams() | ParamState.ofCash);
			else o.setParams(o.getParams() &(~ParamState.ofCash));
			
			o.setRemark(remark.getText().toString().trim());
			
			Agreements agr = (Agreements) ((Spinner)findViewById(R.id.spAgr)).getSelectedItem();
			if( agr != null ) {
				o.setAgreement(agr.id);
				o.setFirmCode(agr.idFirm);
			}
			
			if( Features.DELIVERY_ADDRESS ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					KeyValue sel = (KeyValue) spAddress.getSelectedItem();
					if( sel != null )
						o.setAdrCode(sel.key.toString());
				}
			}
			
			String number = ((EditText)findViewById(R.id.edNumber)).getText().toString();
			o.setDocNumber(number);
			DocHelper.saveDocNumber(order.getTableName(), number);
			
			if (updateSumType)
				order.updateItemsCost(o.getSumType());
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

	protected void initDocNumber(String id) {
		((IOrder)order.getData()).setFirmCode(id);
		String number = DocHelper.makeDocNumber(order);
		
		((IOrder)order.getData()).setDocNumber(number);
		EditText ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(number);
	}
}
