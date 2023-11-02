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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import android.widget.Toast;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private static final int DIALOG_DLVDATE_PICKER_ID = 3;
	
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;

	private OrgEx org;

	private EditText edDlvText;

	private Button btnDlvDate;

	private boolean haveUnpayDelivery;
	
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
	
	void loadSklads(List<String> availSklads, String selected, Spinner spWh, ConfigImpl config) {
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		Config c = config.getData();
		c.key = "Склады";
		config.read();
		
		if(selected.length() > 0 && availSklads.contains(selected) == false) {
			availSklads.add(selected);
		}

		int sel = DialogHelper.makeListWithKey(c.value, values, selected);
		int selectedIndex = -1;
		ArrayList<KeyValueIndex> indexs = new ArrayList<KeyValueIndex>();
		int index  = 0;
		for(KeyValue kv:values) {
			if(availSklads.contains(kv.key.toString())) {
				if(sel == index)
					selectedIndex = indexs.size();
				indexs.add(new KeyValueIndex(kv, index));
			}
			index++;
		}
	
		ArrayAdapter<KeyValueIndex> aa = new ArrayAdapter<KeyValueIndex>(spWh.getContext(), R.layout.simple_spinner_layout, indexs);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spWh.setAdapter(aa);
		if( selectedIndex >= 0 && selectedIndex < spWh.getCount())
			spWh.setSelection(selectedIndex);
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		org = (OrgEx)oi.getData();
		org.id = o.id;
		oi.read();
		oi.close();

		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		String[] availSklads = org.sklads.split(",");
		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		
		if( !editMode ) 
			initOrder(o, org);
		else {
			spWh.setEnabled(order.getData().items.size() == 0);
		}

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);
		
		List<CharSequence> dscval = Arrays.asList(new CharSequence[] {
				"", "Клиентская скидка", "Акционная скидка", "Бонусные бутылки",
		});
		Spinner spDsc = (Spinner)findViewById(R.id.spDiscount);
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, dscval);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spDsc.setAdapter(aa);
		int sel = dscval.indexOf(o.discount);
		if(sel >= 0)
			spDsc.setSelection(sel);

		List<CharSequence> prval = Arrays.asList(new CharSequence[] {
				"", "Нет", "Да",
		});
		Spinner spPr = (Spinner)findViewById(R.id.spPR);
		ArrayAdapter<CharSequence> apr = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, prval);
		apr.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spPr.setAdapter(apr);
		spPr.setSelection(o.pr + 1);
		
		
		loadSklads(Arrays.asList(availSklads), o.whCode, spWh, config);
//		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, o.whCode);

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
					ArrayAdapter<KeyValue> adr = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
					spAddress.setAdapter(adr);
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
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());
		
		haveUnpayDelivery = haveUnpayedDocs();
		
		btnDlvDate = (Button)findViewById(R.id.btnDlvDate);
		btnDlvDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, new Date().getTime());
				startActivityForResult(i, DIALOG_DLVDATE_PICKER_ID);
			}
		});
		
		if(haveUnpayDelivery && editMode)
			refreshDlvDate();
		
		edDlvText = (EditText) findViewById(R.id.edDlvText);
		
		if(haveUnpayDelivery && editMode)
			edDlvText.setText(o.dlvText);
		
		View v = findViewById(R.id.dlvlay);
		v.setVisibility(haveUnpayDelivery ? View.VISIBLE : View.GONE);
		
		findViewById(R.id.tvDlvInfo).setVisibility(haveUnpayDelivery ? View.VISIBLE : View.GONE);;
		
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
		}else if( data != null && requestCode == DIALOG_DLVDATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			((OrderEx)order.getData()).dlvDate = newDate;
			refreshDlvDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}
	
	private void refreshDlvDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		btnDlvDate.setText(sd.format(((OrderEx)order.getData()).dlvDate));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(Order o, Org org) {
		o.sumType = org.costype;
		
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
	
	boolean checkDoc() {
		if (haveUnpayDelivery && (((OrderEx)order.getData()).dlvDate.getTime() == 0 || edDlvText.getText().toString().trim().length() == 0)){ 
			Toast.makeText(this, R.string.should_input_dlv_text, Toast.LENGTH_SHORT).show();
			return false;
		}
		if(((Spinner)findViewById(R.id.spDiscount)).getSelectedItemPosition() == 0) {
			Toast.makeText(this, R.string.need_select_discount, Toast.LENGTH_SHORT).show();
			return false;
		}
		if(((Spinner)findViewById(R.id.spPR)).getSelectedItemPosition() == 0) {
			Toast.makeText(this, R.string.need_select_pr, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			if (checkDoc()){
				Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
				int costType = spPrices.getSelectedItemPosition();
				
				if (editMode && (order.getSumType() != costType && costType >= 0))
					askToApplyNewSumType(v.getContext(), costType);
				else 
					okDone(false);
			}
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

			if( suppl >= 0 )
				o.supplyer = suppl;
			if( costType >= 0 )
				o.sumType = costType;
			
			Spinner spWh = (Spinner) findViewById(R.id.spWh);
			KeyValueIndex kv = (KeyValueIndex)spWh.getSelectedItem();
			if( kv != null ) {
				o.whCode = kv.key;
				o.whIndex = kv.index;
			}
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);
			
			Spinner spdsc = (Spinner)findViewById(R.id.spDiscount);
			o.discount = spdsc.getSelectedItem().toString();
			
			o.pr = (((Spinner)findViewById(R.id.spPR)).getSelectedItemPosition() - 1);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if (haveUnpayDelivery)
				o.dlvText = edDlvText.getText().toString().trim();
			
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
	
	private boolean haveUnpayedDocs() {
		boolean result = false;
		Date now = new Date();
		
		com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(org.id);
		
		for(Document<?> doc : dl) {
			Delivery dlv = (Delivery)doc.getData();
			
			if(dlv.sumD > 0 && dlv.payDate.compareTo(now) < 0) {
				result = true;
				break;
			}
		}
		
		dl.close();
		
		return result;
	}
}


class KeyValueIndex {
	public String key;
	public String value;
	public int index;
	
	public KeyValueIndex(KeyValue v, int index) {
		this.key = v.key.toString();
		this.value = v.value.toString();
		this.index = index;
	}
	
	@Override
	public String toString() {
		return value;
	}
}