/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Motivation;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.Store;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	protected static final int DIALOG_DLV_DATE_PICKER_ID = 2;
	
	private boolean editMode = false;
	boolean loading = true;
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	int selObject;
	Spinner spTypesImplement;
	Spinner spMotivation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		
		spTypesImplement = (Spinner) findViewById(R.id.spTypesImplement);
		spMotivation = (Spinner) findViewById(R.id.spMotivation);
		
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
	
	void loadSpinner(Class<? extends DataObject> obj, final String selected, Spinner sp){
		try {
			loadSpinner(obj, selected, sp, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void loadSpinner(Class<? extends DataObject> obj, final String selected, Spinner sp, String where) throws NoSuchFieldException {
		final List<DataObject> list = new ArrayList<DataObject>();
		final Field idField = obj.getField("id");
		selObject = -1;
		
		DataTraveler.travel(obj, new DataTraveler.Travel<DataObject>(true) {

			@Override
			public boolean travel(DataTraveler<DataObject> item) {
				if(idField != null) {
					try {
						Object idVal = idField.get(item.data);
						if(idVal != null && idVal.equals(selected))
							selObject = list.size();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				list.add(item.data);
				return true;
			}
		}, where, "\"index\"");
		
		
		ArrayAdapter<DataObject> aa = new ArrayAdapter<DataObject>(sp.getContext(), R.layout.simple_spinner_layout, list);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( selObject >= 0 )
			sp.setSelection(selObject);
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx  o = (OrderEx) order.getData();
		
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

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		try {
			Spinner sp = (Spinner) findViewById(R.id.spFirma);
			loadSpinner(FirmEx.class, o.firmCode, sp);
//			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//					if(!loading) {
//						FirmEx f = (FirmEx)arg0.getSelectedItem(); 
//						updateChildSpinners(f.id);
//					}
//				}
//				@Override public void onNothingSelected(AdapterView<?> arg0) {}
//			});
			
//			String where = "idFirm='" + o.firmCode + "'";
			String where = "";
			loadSpinner(PriceTypes.class, o.prcType, spPrices, where);
			loadSpinner(Store.class, o.idStore, (Spinner) findViewById(R.id.spWh), where);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ConfigImpl config = new ConfigImpl();		
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
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});

		findViewById(R.id.tvDlvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DLV_DATE_PICKER_ID);
			}
		});
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		findViewById(R.id.tvSum).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				InputNumberDlg.open(CreateOrder.this, new InputNumber() {
					@Override public int getValue() { return ((OrderEx)order.getData()).agentSum; }
					@Override
					public void applayInput(int value, Object... params) {
						((OrderEx)order.getData()).agentSum = value;
						refreshAgentSum();
					}
				}, Consts.SUM_SCALE, false, "Введите сумму");
			}
		});
		
		loadSpinner(Motivation.class, o.mid, spMotivation);
		((ArrayAdapter<Motivation>)spMotivation.getAdapter()).insert(new Motivation(), 0);

		for (int i = 0; i < spMotivation.getCount(); i++) {
			Motivation m = (Motivation) spMotivation.getItemAtPosition(i);
			
			if (m != null && m.id.equals(o.mid)) {
				spMotivation.setSelection(i, true);
				break;
			}
		}
			
		
		DialogHelper.loadSpinnerFromConfig(config, "ВидРеализации", new ArrayList<CharSequence>(), 
				spTypesImplement, ((OrderEx)o).idxTypeImpl);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDelay();
		refreshDate();
		refreshAgentSum();
	}
	
//	protected void updateChildSpinners(String firmId) {
//		try {
//			String where = "idFirm='" + firmId + "'";
//			loadSpinner(Store.class, "", (Spinner) findViewById(R.id.spWh), where);
//			loadSpinner(PriceTypes.class, "", (Spinner) findViewById(R.id.spPrices), where);
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();
		loading = false;
	}
	
	void refreshAgentSum() {
		((TextView)findViewById(R.id.tvSum)).setText(Util.IntToScaleStr(((OrderEx)order.getData()).agentSum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && (requestCode == DIALOG_DATE_PICKER_ID || requestCode == DIALOG_DLV_DATE_PICKER_ID) ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			OrderEx oe = (OrderEx)order.getData();
			if(requestCode == DIALOG_DATE_PICKER_ID) {
				oe.date = newDate;
				initDlvDate(oe);
			}else 
				oe.dlvDate = newDate;
			
			refreshDate();
		}
	}

	protected void initDlvDate(OrderEx oe) {
		Calendar c = Calendar.getInstance();
		c.setTime(oe.date);
		c.add(Calendar.DATE, -1);
		
		oe.dlvDate = c.getTime();
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		OrderEx oe = (OrderEx) order.getData();
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(oe.date));		
		((TextView)findViewById(R.id.tvDlvDate)).setText(sd.format(oe.dlvDate));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(Order o, OrgEx org) {
		o.prcType = org.prcType;
		
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
		
		initDlvDate((OrderEx)o);
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

	private void updateDisplayDelay() {
		String text =  "отсрочка: " + order.getData().delay;
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new UnderlineSpan(), 0, text.length(), 0);
		((TextView)findViewById(R.id.tvDelay)).setText(ss);
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
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(o.date);
//			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( suppl >= 0 ) {
				o.supplyer = suppl;
				o.firmCode = ((FirmEx)spFirma.getSelectedItem()).id;
			}
			if( costType >= 0 ) {
				o.prcType = ((PriceTypes)spPrices.getSelectedItem()).id;
				o.sumType = PriceTypes.getPriceTypeColumn(o.prcType);
			}
			
			Store store = (Store) ((Spinner)findViewById(R.id.spWh)).getSelectedItem();
			if(store != null)
				o.idStore = store.id;
			
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
			
			o.idxTypeImpl = spTypesImplement.getSelectedItemPosition();
			
			Motivation m = (Motivation) spMotivation.getSelectedItem();
			
			if (m != null)
				o.mid = m.id;
			else
				o.mid = "";
			
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
