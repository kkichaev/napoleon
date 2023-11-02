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
import java.util.Locale;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder_ex);
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
		
		
		OrgSumImpl osi = new OrgSumImpl();
		OrgSum os = osi.getData();
		os.id = o.id;
		os.type = DebtDoc.instance().getName();
		osi.read();
		osi.close();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();

		Org org = (Org) oi.getData();		
		String ret = OrgUtils.makeOrgInfo((OrgEx) org, order);
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) 
			initOrder(o, org);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

//		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", new ArrayList<KeyValue>(), spPrices, o.prcType);

//		config.getData().key = "МожноИзменятьЦену";
//		try {
//			if (config.read() && Integer.parseInt(config.getData().value) == 0)
//				spPrices.setEnabled(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		config.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvTotalSum);
		tv.setText(Html.fromHtml("<b>" + Util.IntToScaleStr(order.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>"));
		
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
		
		remark = (EditText)findViewById(R.id.edExpRemark);
		remark.setText(o.expeditorRemark);
		
		EditText num = (EditText)findViewById(R.id.edIncass);
		num.setText(Util.IntToScaleStr(o.incass, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		((CheckBox)findViewById(R.id.cbFactPay)).setChecked(o.factPay > 0);
		((CheckBox)findViewById(R.id.cbThinkInOffice)).setChecked(o.thinkInOffice > 0);

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
        updateDisplayDelay();
		refreshDate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date minDate = new Date(Util.getDate().getTime() + 3600 * 24 * 1000);
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, 7);
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, minDate.getTime());
			if(ct >= minDate.getTime() && ct < c.getTime().getTime()) {
				Date newDate = new Date(ct);
				order.getData().date = newDate;
				refreshDate();
			} else {
				Toast.makeText(this, "Дата выходит за разрешенный диаппазон", Toast.LENGTH_LONG).show();
			}
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
	private void initOrder(Order o, Org org) {
		// move to ORderImplEx::postCreate
	}
	

//	private void dateworkday(Order o) {
//		Calendar c = Calendar.getInstance();
//		c.setTime(o.date);
//		c.add(Calendar.DAY_OF_MONTH, 1);
//		
//		if( c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
//			c.add(Calendar.DAY_OF_MONTH, 1);
//		
//		o.date = c.getTime();
//	}

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
//			deleteEmptyOrder();			
			if(!editMode)
				order.open(CreateOrder.this);
			finish();
		}
	}
	
//	private void deleteEmptyOrder() {
//		if(!editMode) {
//			order.open(this);
//			if( order.isEmpty() )
//				order.delete();
//		}
//	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
//			long outSum = OrgUtils.getOutDebt(order.getId());
//			if( outSum > 0 ) {
//				long incass = 0, willSum = 0;
//				EditText num = (EditText)findViewById(R.id.edIncass);
//				incass = Util.StrToScale(num.getText().toString(), Consts.SUM_SCALE);
//				num = (EditText)findViewById(R.id.edWillSum);
//				willSum = Util.StrToScale(num.getText().toString(), Consts.SUM_SCALE);
//				
//				if( incass == 0 && willSum == 0 ) {
//					Toast.makeText(CreateOrder.this, R.string.outDebtErr, Toast.LENGTH_SHORT).show();
//					return;
//				}
//			}
			
//			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//			int costType = spPrices.getSelectedItemPosition();
			
//			if (editMode && (order.getSumType() != costType && costType >= 0))
//				askToApplyNewSumType(v.getContext(), costType);
//			else 
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
//			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//			int costType = spPrices.getSelectedItemPosition();

			if( suppl >= 0 )
				o.supplyer = suppl;
//			if( costType >= 0 ) {
//				o.sumType = costType;
//				o.prcType = ((KeyValue)spPrices.getSelectedItem()).key.toString();
//			}
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			remark = (EditText)findViewById(R.id.edExpRemark);
			o.expeditorRemark = remark.getText().toString();
			
			
			EditText num = (EditText)findViewById(R.id.edIncass);
			o.incass = Util.StrToScale(num.getText().toString(), Consts.SUM_SCALE);
			
			o.thinkInOffice = ((CheckBox)findViewById(R.id.cbThinkInOffice)).isChecked() ? 1 :0;
			o.factPay = ((CheckBox)findViewById(R.id.cbFactPay)).isChecked() ? 1 :0;
			
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
				order.open(CreateOrder.this);
				//Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
		
//		private void askToApplyNewSumType(Context context, final int newSumType){
//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setTitle("Внимание");
//			builder.setMessage("Тип цены был изменен, пересчитать заказ?");
//
//			builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					okDone(true);
//				}
//			});
//			
//			builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					okDone(false);
//				}
//			});
//			
//			builder.create().show();
//		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				if(!editMode)
					order.open(this);
//				deleteEmptyOrder();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}
