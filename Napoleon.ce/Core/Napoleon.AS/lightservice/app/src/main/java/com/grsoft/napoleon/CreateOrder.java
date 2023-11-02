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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

@SuppressWarnings("deprecation")
public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	private TextView tvDiscount;

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

	void loadFirms(ConfigImpl config, final OrderEx o, final OrgEx org) {
		Config c = config.getData();
		c.key = "Организации";
		config.read();

		final Set<String> availDF = new HashSet<>();
		for(Dogovor d : org.dogovors) {
			availDF.add(d.firm);
		}
		List<KeyValue> firms = new ArrayList<>();
		int sel = DialogHelper.makeListWithKeyFilter(c.value, firms, o.firmCode, new DialogHelper.Filter() {
			@Override public boolean contains(KeyValue value) { return availDF.contains(value.key.toString()); }
		});

		final ArrayAdapter<KeyValue> akv = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, firms);
		Spinner spFirm = findViewById(R.id.spFirm);
		spFirm.setAdapter(akv);
		spFirm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				KeyValue selF = akv.getItem(position);
				List<Dogovor> dogs = new ArrayList<>();

				int seld = -1;
				for(Dogovor d : org.dogovors) {
					if(!d.firm.equals(selF.key.toString())) continue;
					if(d.id.equals(o.dogovor)) seld = dogs.size();
					dogs.add(d);
				}

				Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
				ArrayAdapter<Dogovor> aa = new ArrayAdapter<Dogovor>(CreateOrder.this, R.layout.simple_spinner_layout, dogs);
				aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
				spDog.setAdapter(aa);
				if(seld >= 0) spDog.setSelection(seld);
				else if(aa.getCount() > 0) spDog.setSelection(0);
			}
			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
		if( sel >= 0)
			spFirm.setSelection(sel);
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

		OrgEx org = (OrgEx) oi.getData();
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) 
			initOrder(o, org);

		ConfigImpl config = new ConfigImpl();

		loadFirms(config, o, org);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", new ArrayList<CharSequence>(), spPrices, o.sumType);

		Spinner spSklad = (Spinner) findViewById(R.id.spWh);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spSklad, o.whCode);
		spSklad.setEnabled(o.items.size() == 0);

		((Spinner) findViewById(R.id.spDogovor)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String text = "";
				Dogovor sel = (Dogovor) parent.getItemAtPosition(position);
				if(sel != null) {
					int limit = sel.limit != 0 ? sel.limit - sel.balance : 0;
					text = "Свободный лимит: <b><font color='blue'>" + Util.IntToScaleStr(limit, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font></b>";
				}
				((TextView)findViewById(R.id.tvInfo)).setText(Html.fromHtml(text));
			}

			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);

		((CheckBox)findViewById(R.id.cbMoneyProc)).setChecked(o.moneyProc != 0);
//		((CheckBox)findViewById(R.id.cbItemsProc)).setChecked(o.itemsProc != 0);
		((CheckBox)findViewById(R.id.cbCert)).setChecked(o.cert != 0);

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
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
		refreshDate();

		tvDiscount = findViewById(R.id.tvDiscount);
		tvDiscount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DiscountInputDlg.open(CreateOrder.this, new InputNumber() {
					@Override public long getValue() { return ((OrderEx)order.getData()).discount; }
					@Override public void applayInput(int value, Object... params) {
						((OrderEx)order.getData()).discount = -value;
						updateDiscount();
					}
				}, Consts.SUM_SCALE, true, getString(R.string.discount), DiscountInputDlg.Type.OnlyDiscount);
			}
		});
		updateDiscount();
	}

	private void updateDiscount() {
		int val = ((OrderEx)order.getData()).discount;
		SpannableString content = new SpannableString(getString(R.string.order_disc, Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, true)));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		tvDiscount.setText(content);
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
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		o.sumType = org.costype;
		o.address = org.address;
		
		switch(ConfigHelper.getDateType()){
		case workday:
			dateworkday(o);
			break;
		case nextday:
			datenextday(o);
			break;
		default:
			break;
		}
	}
	
	private void datenextday(Order o) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.date = c.getTime();	
	}

	private void dateworkday(Order o) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if( c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
			c.add(Calendar.DAY_OF_MONTH, 1);
		
		o.date = c.getTime();
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
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(o.date);
//			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();

			Spinner spFirm = findViewById(R.id.spFirm);
			KeyValue selF = (KeyValue) spFirm.getSelectedItem();
			if(selF != null)
				o.firmCode = selF.key.toString();

			Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
			Dogovor selDog = (Dogovor) spDog.getSelectedItem();
			if(selDog != null) {
				o.dogovor = selDog.id;
				o.firmCode = selDog.firm;
			}
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( costType >= 0 )
				o.sumType = costType;

			Spinner spSklad = (Spinner) findViewById(R.id.spWh);
			KeyValue kv = (KeyValue)spSklad.getSelectedItem();
			if( kv != null ) {
				o.whIndex = spSklad.getSelectedItemPosition();
				o.whCode = kv.key.toString();
			}

			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			o.moneyProc = (((CheckBox)findViewById(R.id.cbMoneyProc)).isChecked()) ? 1 : 0;
//			o.itemsProc = (((CheckBox)findViewById(R.id.cbItemsProc)).isChecked()) ? 1 : 0;
			o.cert = (((CheckBox)findViewById(R.id.cbCert)).isChecked()) ? 1 : 0;

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
