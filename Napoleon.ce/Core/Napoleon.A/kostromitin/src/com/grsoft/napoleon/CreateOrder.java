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
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FirmEx;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
	private List<FirmEx> firms = new ArrayList<FirmEx>();
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	
	int selected = -1;
	int maxDiscount = OrdHelper.getMaxDiscount();
	
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
		final OrderEx o = (OrderEx) order.getData();
		
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
		selected = -1;
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<FirmEx>(true) {

			@Override
			public boolean travel(DataTraveler<FirmEx> item) {
				if(o.firmCode.equals(item.data.id))
					selected = firms.size();
				firms.add(item.data);
				return true;
			}
		}, "");
		ArrayAdapter<FirmEx> fa = new ArrayAdapter<FirmEx>(this, R.layout.simple_spinner_layout, firms);
		fa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spFirma.setAdapter(fa);
		if( selected >= 0 )
			spFirma.setSelection(selected);
		spFirma.setEnabled(false);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", new ArrayList<KeyValue>(), spPrices, o.prcType);

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

		
		CheckBox cb = (CheckBox)findViewById(R.id.cbCreateOrderCash);
		cb.setChecked((o.params & ParamState.ofCash) != 0);
//		cb.setOnCheckedChangeListener(updateCB);
		
		cb = (CheckBox)findViewById(R.id.cbTax);
		cb.setChecked(o.useTax != 0);
//		cb.setOnCheckedChangeListener(updateCB);

//		cb = (CheckBox)findViewById(R.id.cbLukoil);
//		cb.setChecked(o.lukoil != 0);
//		cb.setOnCheckedChangeListener(updateCB);
		
//		updateCheckBoxes();
		
		updateDiscount();
		if(o.items.size() == 0) {
			findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) { changeDiscount(); }
			});
		}
		
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
	
	protected void changeDiscount() {
		DiscountInputDlg.open(this, new InputNumber() {
			
			@Override
			public int getValue() {
				return -((OrderEx)order.getData()).discount;
			}
			
			@Override
			public void applayInput(int value, Object... params) {
				value = -value;
				if( value <= maxDiscount ) {
					((OrderEx)order.getData()).discount = value;
					updateDiscount();
				} else {
					showDialog(R.id.cost_below_min);
				}
			}
		}, Consts.SUM_SCALE, false, "Введите скидку", DiscountInputDlg.Type.Both);
	}

	private void updateDiscount() {
		OrderEx oe = (OrderEx)order.getData();
		
		String text = "";
		text += (oe.discount >= 0 ) ? "Скидка," : "Наценка";
		text += "% <font color='blue'><b><u>" + Util.IntToScaleStr(Math.abs(oe.discount), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</u></b></font>";
		
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setText(Html.fromHtml(text));
	}

	CompoundButton.OnCheckedChangeListener updateCB = new CompoundButton.OnCheckedChangeListener() {
		@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { updateCheckBoxes(); }
	};
	
	void updateCheckBoxes() {
		OrderEx o = (OrderEx) order.getData();
		boolean canChangeCB = order.isEditable() && o.items.size() == 0;
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbCreateOrderCash);
		boolean isCash = cb.isChecked();
		cb.setEnabled(canChangeCB);
		
		cb = (CheckBox)findViewById(R.id.cbTax);
		boolean useTax = cb.isChecked();
		cb.setEnabled(canChangeCB && !isCash);

		cb = (CheckBox)findViewById(R.id.cbLukoil);
		boolean lukoil = cb.isChecked();
		cb.setEnabled(canChangeCB && !isCash && useTax);
		
		int firmType = isCash || !useTax ? FirmEx.FIRM_TYPE_NAL :
			lukoil ? FirmEx.FIRM_TYPE_LUKOIL :
				FirmEx.FIRM_TYPE_OTHER;
		
		for( int i=0; i<firms.size(); i++) {
			FirmEx fe = firms.get(i);
			if(fe.firmType == firmType) {
				((Spinner)findViewById(R.id.spFirma)).setSelection(i);
				break;
			}
		}
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
		if( id == R.id.cost_below_min) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка ввода");
			b.setMessage("Скидка больше максимальной");
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
			});
			return b.create();
		}
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
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			FirmEx fe  = (FirmEx) spFirma.getSelectedItem();
			if( fe != null ) {
				o.supplyer = spFirma.getSelectedItemPosition();
				o.firmCode = fe.id;
//				o.secondWH = fe.firmType == FirmEx.FIRM_TYPE_OTHER ? 1 : 0;
			}
			
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			KeyValue sel = (KeyValue) spPrices.getSelectedItem();

			if( sel != null ) {
				o.sumType = spPrices.getSelectedItemPosition();
				o.prcType = sel.key.toString();
			}
			
			boolean canChangeCB = order.isEditable() && o.items.size() == 0;
			if( canChangeCB ) {
				CheckBox cb = (CheckBox)findViewById(R.id.cbCreateOrderCash);
				boolean isCash = cb.isChecked(); 
				if( isCash ) o.params |= ParamState.ofCash;
				else o.params &= (~ParamState.ofCash);
				
				cb = (CheckBox)findViewById(R.id.cbTax);
				o.useTax = !isCash && cb.isChecked() ? 1 : 0;
				
//				cb = (CheckBox)findViewById(R.id.cbLukoil);
//				o.lukoil = o.useTax > 0 && cb.isChecked() ? 1 : 0;
			}
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if( Features.DELIVERY_ADDRESS ) {
				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
				if( spAddress != null ) {
					sel = (KeyValue) spAddress.getSelectedItem();
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
