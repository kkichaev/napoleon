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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Accounts;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DlvTypes;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PayTypes;
import com.grsoft.dataobjects.ShipTypes;
import com.grsoft.dataobjects.TypeName;
import com.grsoft.dataobjects.impl.AccountsImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.DiscountInputDlg.Type;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CommentChoice;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private static final int ASK_PRICE_CHANGE = 2;
	
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	private boolean editMode = false;
	boolean initing = true;

	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<KeyValue> priceType = new ArrayList<KeyValue>();
	
	Date orderDate;
	int delay;
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	EditText remark;
	CommentChoice commentChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		init();
	}
	
	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		o.date = orderDate;
		o.delay = delay;
	
		o.date = timeHandler.adjustTime(o.date);
		
		if (o.created == null)
			o.created = new Date();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		KeyValue selPrice = (KeyValue)spPrices.getSelectedItem();

		if( suppl >= 0 )
			o.supplyer = suppl;
		if( selPrice != null ) {
			o.taxType = selPrice.key.toString();
			o.sumType = spPrices.getSelectedItemPosition();
		}
		
		KeyValue val;
		val = (KeyValue) ((Spinner)findViewById(R.id.spDlvType)).getSelectedItem();
		if( val != null )
			o.dlvType = val.key.toString();
		
		val = (KeyValue) ((Spinner)findViewById(R.id.spShipType)).getSelectedItem();
		if( val != null )
			o.shipType = val.key.toString();
				
		val = (KeyValue) ((Spinner)findViewById(R.id.spPayType)).getSelectedItem();
		if( val != null )
			o.payType = val.key.toString();
		
//		val = (KeyValue) ((Spinner)findViewById(R.id.spAccounts)).getSelectedItem();
//		if( val != null )
//			o.account = val.key.toString();

		o.remark = remark.getText().toString();
		
		if( Features.DELIVERY_ADDRESS ) {
			Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
			if( spAddress != null ) {
				KeyValue sel = (KeyValue) spAddress.getSelectedItem();
				if( sel != null )
					o.adrCode = sel.key.toString();
			}
		}	
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
	
	private void initOrder(OrderEx o, OrgEx oe) {
//		o.taxType = oe.taxType;
//		o.payType = oe.payType;
//		o.delay = oe.dayDeff;
		o.ido = oe.ido;
//		o.discount = oe.discount;
	}
	
	private void loadSpinner(int id, TypeName data, String key, String where) {
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		int selected = -1;
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, where, "name");
		while( bdo ) {
			if(data.type.equals(key))
				selected = values.size();
			
			values.add(new KeyValue(data.type, data.name));
			bdo = r.selectNext(data);
		}
		
		Spinner sp = (Spinner)findViewById(id);
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( selected >= 0 && selected < sp.getCount())
			sp.setSelection(selected);
	}

	protected void onAccountSelect(String key) {
		OrderEx o = (OrderEx)order.getData(); 
		AccountsImpl ai = new AccountsImpl();
		Accounts a = ai.getData();
		a.type = key;
		a.ido = o.ido;
		
		if( ai.read() ) {
			if( !editMode || !initing ) {
				o.taxType = a.taxType;
				o.payType = a.payType;
				o.delay = a.dayDeff;
				o.discount = a.discount;
				o.account = key;				
			}
			refreshPriceTypes(o.taxType, a.taxType);
			refreshVisualData(a);
		}
		ai.close();
		initing = false;
	}
	
	private void refreshVisualData(Accounts a) {
		updateDiscount();
		
		CheckBox cb;
		
		cb = (CheckBox)findViewById(R.id.cbContrDate);
		Date d = new Date(72, 10, 10);
		if( a.contrDate.after(d) ) {
			cb.setChecked(true);
			cb.setText("Дата контракта " + Util.simpleDateFormat.format(a.contrDate));
		} else {
			cb.setChecked(false);
			cb.setText("Нет даты контракта");
		}
		
		cb = (CheckBox)findViewById(R.id.cbContrNum);
		if(a.contrNumber.length()>0) {
			cb.setChecked(true);
			cb.setText("Номер контракта " + a.contrNumber);
		} else {
			cb.setChecked(false);
			cb.setText("Нет номера контракта");
		}
		String info;
		
		info = "";
		info += "Лимит кредита <b>" + Util.IntToScaleStr(a.limit, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.</b><br>";
		info += "Остаток кредита <b>" + Util.IntToScaleStr(a.rest, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.</b><br>";
		info += "Отсрочка <b>" + Util.IntToScaleStr(a.dayDeff, 1, Util.DEC_DELIM, true) + "</b><br>";
		
		((TextView)findViewById(R.id.tvInfo)).setText(Html.fromHtml(info));

	}
	
	private void refreshPriceTypes(String newTaxType, String accTaxType) {

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		CostTypes ct = new CostTypes();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
		int selected = -1;
		boolean bdo = r.select(ct, table, "", "name");
		while( bdo ) {
			if( ct.id.equals(accTaxType) || ct.userid.length() == 0 ) {
				if( ct.id.equals(newTaxType))
					selected = priceType.size();
				priceType.add(new KeyValue(ct.id, ct.name));
			}
			bdo = r.selectNext(ct);
		}
		r.close();
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, priceType);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spPrices.setAdapter(aa);
		if( selected >= 0 && selected < spPrices.getCount())
			spPrices.setSelection(selected);
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
	    order.read(orderRowId);

	    OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);
		
		if( !editMode ) 
			initOrder(o, oe);

		orderDate = o.date;
		delay = o.delay;

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				findViewById(R.id.spPrices).setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		((Spinner)findViewById(R.id.spAccounts)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onAccountSelect(((KeyValue)arg0.getSelectedItem()).key.toString());
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		

		loadSpinner(R.id.spDlvType, new DlvTypes(), o.dlvType, "");
		loadSpinner(R.id.spShipType, new ShipTypes(), o.shipType, "");
		loadSpinner(R.id.spPayType, new PayTypes(), o.payType, "");
		loadSpinner(R.id.spAccounts, new Accounts(), o.account, "ido='" + o.ido + "'");
		
		findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) { changeDiscount(); }
		});
				
//		if( Features.DELIVERY_ADDRESS ) {
//			View v = findViewById(R.id.ftrAddress);
//			if( v != null ) {
//				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
//				if( spAddress != null ) {
//					v.setVisibility(View.VISIBLE);
//					ArrayList<KeyValue> addresses = new ArrayList<KeyValue>();
//					selected = -1;
//					for(OrgAddress addr : oi.getData().orgAddress) {
//						KeyValue kv = new KeyValue(addr.id, addr.name);
//						if( kv.key.toString().equals(o.adrCode))
//							selected = addresses.size();
//						addresses.add(kv);
//					}
//					aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
//					spAddress.setAdapter(aa);
//					if( selected >= 0 && selected < spAddress.getCount())
//						spAddress.setSelection(selected);
//				}
//			}
//		}
		
		remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		commentChoice = new CommentChoice(remark);

		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, orderDate);
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		refreshDate();
		
		if( o.account != null && o.account.length() > 0 ) {
			AccountsImpl ai = new AccountsImpl();
			Accounts a = ai.getData();
			a.ido = o.ido;
			a.type = o.account;
			if( ai.read() )
				refreshVisualData(a);
			ai.close();
		} else
			updateDiscount();

	
		View btnOK = ((Button)findViewById(R.id.btnOK));
		btnOK.setOnClickListener(new OKClickListener());
		btnOK.setEnabled(!order.isExported());
		((Button)findViewById(R.id.btnCancel)).setOnClickListener(new CancelClickListener());
	}
	
	private void updateDiscount() {
		int dsc = ((OrderEx)order.getData()).discount;
		String value = Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv;
		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setText(value + "%");
	}

	protected void changeDiscount() {
		DiscountInputDlg.open(this, new InputNumber() {
			@Override public int getValue() { return ((OrderEx)order.getData()).discount; }
			@Override public void applayInput(int value, Object... params) {
				((OrderEx)order.getData()).discount = value;
				updateDiscount();
			}
		}, Consts.SUM_SCALE, false, "Введите наценку", Type.OnlyNac);
	}
	
	class OKClickListener extends OnClickListenerToNotify{

		@Override
		public void onClick(View v)	{
			
			KeyValue selPrice = (KeyValue)((Spinner)findViewById(R.id.spPrices)).getSelectedItem();
			if( selPrice != null && selPrice.key.toString().equals(((OrderEx)order.getData()).taxType) == false ) {
				showDialog(ASK_PRICE_CHANGE);
				return;
			}
			
			updateOrder(false);
			super.onClick(v);			
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			orderDate = new Date(ct);
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(orderDate));		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
//			case DIALOG_DATE_PICKER_ID:
//				return dateHandler.createDialog();
			case DIALOG_TIME_PICKER_ID:
				return timeHandler.createDialog();
				
			case ASK_PRICE_CHANGE:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.alert);
				builder.setMessage("Тип цены был изменен, пересчитать заказ?");

				builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { updateOrder(true); }
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { 
						dialog.dismiss();
					}
				});
				
				return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
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

	protected void updateOrder(boolean updateCost) {
		update(order);
		
		if( updateCost )
			order.updateItemsCost(order.getSumType());
		order.write();
		
		if(!editMode)
			Warehouse.open(CreateOrder.this, order, false);
		
		finish();
	}
}
