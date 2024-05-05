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
import java.util.Collections;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderCard;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

@SuppressWarnings("deprecation")
public class CreateOrder extends BaseActivity
{
	private static final String PICKUP_KEY = "1";
	private OrderImplEx order = (OrderImplEx)OrderDoc.instance().create();
	boolean refreshOrderSum = false;
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	int deliveryFlag = 0;

	List<KeyValue> sklads = new ArrayList<>();

//	DateHandler dateHandler;
	TimeHandler timeHandler;
	boolean checkDeliveryDay = false;
	List<KeyValue> cards;
	
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

		checkDeliveryDay = order.isEditable() && ((OrgEx)oi.getData()).dscMode == OrgEx.DISCOUNT_BY_DELIVERY;

		OrgEx org = (OrgEx) oi.getData();
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		deliveryFlag = org.delivery;
		if( !editMode )
			initOrder(o, org);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<>(), spFirma, o.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", new ArrayList<>(), spPrices, o.prcType);
		o.sumType = spPrices.getSelectedItemPosition();

		View trSklads = findViewById(R.id.trSklads);

		if (Features.WH_QTY) {
			Spinner spSklads = (Spinner) findViewById(R.id.spSklad);
			DialogHelper.loadSpinnerWithKeyW(config, "Склады", sklads, spSklads, o.whCode, false);
			if(sklads.size() != 0) {
				trSklads.setVisibility(View.VISIBLE);
				spSklads.setEnabled(o.items.size() == 0);
				if(o.items.size() == 0) {
					spSklads.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							KeyValue kv = (KeyValue) spSklads.getAdapter().getItem(position);
							if(kv != null && !kv.key.equals(o.whCode)) {
								o.cards.clear();
								o.whCode = kv.key.toString();
								refreshCards();
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {}
					});
				}
			}
		}else
			trSklads.setVisibility(View.GONE);

		spPrices.setEnabled(false);

		Spinner spdt = findViewById(R.id.spDlvType);
		DialogHelper.loadSpinnerWithKey(config, "СпособДоставки", new ArrayList<>(), spdt, o.deliveryType);
		if(o.deliveryType.length() == 0 && spdt.getCount() > 0) {
			spdt.setSelection(0);
		}
		spdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				KeyValue sel = (KeyValue) parent.getAdapter().getItem(position);
				if(!sel.key.equals(PICKUP_KEY)) {
					if(order.setDeliveryDate((OrgEx) oi.getData())) {
						refreshDate();
						clearCards();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		config.close();

		refreshCards();

		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

//		if( (o.params & ParamState.ofCash) != 0 )
//			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = CalendarActivity.open(CreateOrder.this, order.getDate(), CreateOrder.this::isDateEnabled);
//				Intent i = new Intent(CreateOrder.this, CalendarActivityEx.class);
//				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
//				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
//				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
//				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
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

	void refreshCards() {
		cards = ((CostStrategyEx)CostStrategy.defaultInstance).clientCards(order.getId(), (OrderEx) order.getData());
		Collections.sort(cards, (o1, o2) -> o1.key.toString().compareTo(o2.key.toString()));
		ListView v = findViewById(R.id.lvCards);
		v.setAdapter(new CardAdapter());
	}

	class CardAdapter extends BaseAdapter {

		@Override public int getCount() {return cards.size();}
		@Override public Object getItem(int position) {return cards.get(position);}
		@Override public long getItemId(int position) {return position;}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null) {
				view = View.inflate(CreateOrder.this, R.layout.card_row, null);
			}
			KeyValue kv = (KeyValue) getItem(position);
			TextView tv;
			tv = view.findViewById(R.id.tvName);
			tv.setText(kv.value.toString());
			tv = view.findViewById(R.id.tvNumber);
			tv.setText(kv.key.toString());

			CheckBox cb = view.findViewById(R.id.cbActive);
			cb.setChecked(order.containsCard(kv.key.toString()));
			cb.setOnClickListener(v -> {
				refreshOrderSum = true;
				order.updateCard(kv.key.toString());
				notifyDataSetChanged();
			});
			return view;
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

			if(checkDeliveryDay) {
				clearCards();
			}
		}
	}

	private void clearCards() {
		refreshCards();
		if(((OrderEx)order.getData()).cards.size() != 0) {
			((OrderEx)order.getData()).cards.clear();
			refreshOrderSum = true;
			Toast.makeText(this, "Проверьте доступность карты лояльности", Toast.LENGTH_LONG).show();
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
		if(deliveryFlag != 0 && (deliveryFlag & 0x7f) < 0x7f) {
			return;
		}
		//o.sumType = org.costype;
		o.prcType = ((OrgEx)org).prcType;
		
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

	boolean isDateEnabled(Date date) {
		Date ch = Util.getDate();

		KeyValue kv = (KeyValue)((Spinner)findViewById(R.id.spDlvType)).getSelectedItem();
		boolean pickup = (kv != null && kv.key.equals(PICKUP_KEY));
		int cmp = date.compareTo(ch);
		if( (pickup && cmp < 0) || (!pickup && cmp <= 0)) {
			return false;
		}

		if((date.getTime() - ch.getTime()) / (1000 * 24 * 3600) >= 8) {
			return false;
		}

		if(pickup)
			return true;

		if(deliveryFlag != 0 && (deliveryFlag & 0x7f) < 0x7f) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);

			int dw = c.get(Calendar.DAY_OF_WEEK);
			if (dw != 0) {
				int f = 1 << (dw - 1);
				return ((deliveryFlag & f) != 0);
			}
			return false;
		}
		return true;
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

			btnCounterOK.setOnClickListener((x)->{});

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
			
			okDone();
		}
		
		private void okDone() {
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(o.date);
//			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
//			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
//			int suppl = spFirma.getSelectedItemPosition();
//			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//			int costType = spPrices.getSelectedItemPosition();
//
//			if( suppl >= 0 )
//				o.supplyer = suppl;
//			if( costType >= 0 )
//				o.sumType = costType;

//			KeyValue selCard = (KeyValue) ((Spinner)findViewById(R.id.spOrgCard)).getSelectedItem();
//			if(selCard != null) {
//				if(!o.card.equals(selCard.key)) {
//					o.card = selCard.key.toString();
//					refreshOrderSum = true;
//				}
//			}

			if (Features.WH_QTY && sklads.size() > 0) {
				Spinner spSklads = (Spinner) findViewById(R.id.spSklad);

				KeyValue sel = (KeyValue) spSklads.getSelectedItem();
				if (sel == null || sel.key.length() == 0) {
					Toast.makeText(CreateOrder.this, "Выберите склад", Toast.LENGTH_LONG).show();
					return;
				}
				if (sel != null) {
					o.whCode = sel.key.toString();
					o.whIndex = spSklads.getSelectedItemPosition();
				}
			}

			KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spDlvType)).getSelectedItem();
			if(kv != null) {
				o.deliveryType = kv.key.toString();
			}

//			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
//			if( cash.isChecked() ) o.params |= ParamState.ofCash;
//			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();

			if (refreshOrderSum)
				order.refreshSum();
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
