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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity {
	private OrderImpl order = (OrderImpl) OrderDoc.instance().create();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();

	private static final int DIALOG_DATE_PICKER_ID = 0;

	private boolean editMode = false;

	DateHandler dateHandler;
	int discount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}

	public static void open(Context context, OrderImpl order) {
		open(context, order, true);
	}

	public static void open(Context context, OrderImpl order,
			boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);

		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);
	}

	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID);

		order.read(orderRowId);
		OrderEx o = (OrderEx) order.getData();

		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		ConfigImpl config = new ConfigImpl();

		if (!editMode) {
			StringBuilder val = new StringBuilder();
			if (config.getValue(val, "Costype"))
				try {
					o.sumType = Integer.parseInt(val.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		DialogHelper.loadSpinnerFromConfig(config, "Организация",
				new ArrayList<CharSequence>(),
				(Spinner) findViewById(R.id.spFirma), o.supplyer);
		DialogHelper.loadSpinnerWithKey(config, "Склады",
				new ArrayList<KeyValue>(),
				(Spinner) findViewById(R.id.spSklad), o.whCode);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);

		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType,
				spPrices, o.sumType);

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		config.close();
		
		Spinner spPayTypes = (Spinner)findViewById(R.id.spPayType);
		for(int i=0; i>spPayTypes.getCount(); i++) {
			if(spPayTypes.getItemAtPosition(i).toString().equals(o.payType)) {
				spPayTypes.setSelection(i);
				break;
			}
		}

		discount = o.discount;
		updateDisplayDiscount();

		TextView tv = (TextView) findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DiscountInputDlg.open(CreateOrder.this, new InputNumber() {
					@Override
					public int getValue() {
						return discount;
					}

					@Override
					public void applayInput(int value, Object... params) {
						discount = value;
						updateDisplayDiscount();
					}
				}, Consts.DISCOUNT_SCALE, false, "Введите скидку");
			}
		});

		EditText remark = (EditText) findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		remark = (EditText) findViewById(R.id.edPayRemark);
		remark.setText(o.payRemark);

		if (o.retail != 0)
			((CheckBox) findViewById(R.id.cbRetail)).setChecked(true);

		if ((o.params & ParamState.ofCash) != 0)
			((CheckBox) findViewById(R.id.cbCreateOrderCash)).setChecked(true);

		dateHandler = new DateHandler((TextView) findViewById(R.id.tvDate),
				o.date, DIALOG_DATE_PICKER_ID);

		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

		findViewById(R.id.btnCancel).setOnClickListener(
				new CancelClickListener());
	}

	private void updateDisplayDiscount() {

		String label = (discount <= 0) ? "Скидка,%:" : "Наценка,%:";
		((TextView) findViewById(R.id.tvDscLabel)).setText(label);
		((TextView) findViewById(R.id.tvDiscount)).setText(Util.IntToScaleStr(
				Math.abs(discount), Consts.DISCOUNT_SCALE, Util.DEC_DELIM,
				false));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DATE_PICKER_ID:
			return dateHandler.createDialog();
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
		if (!editMode) {
			if (order.getData().items == null
					|| order.getData().items.size() == 0)
				order.delete();
		}
	}

	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);

			OrderEx o = (OrderEx) order.getData();
			int newCost = ((Spinner)findViewById(R.id.spPrices)).getSelectedItemPosition();
			boolean changed = (newCost != o.sumType || o.discount != discount);

			if (editMode && changed && o.items != null && o.items.size() > 0)
				askToApplyChanges(v.getContext());
			else
				okDone(false);
		}

		private void okDone(boolean updateDiscount) {
			OrderEx o = (OrderEx) order.getData();
			o.date = dateHandler.getDate();

			if (o.created == null)
				o.created = new Date();

			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			if (suppl >= 0)
				o.supplyer = suppl;
			
			int costType = ((Spinner)findViewById(R.id.spPrices)).getSelectedItemPosition();
			o.sumType = costType;

			KeyValue value;
			value = (KeyValue) ((Spinner) findViewById(R.id.spSklad))
					.getSelectedItem();
			if (value != null)
				o.whCode = value.key.toString();

			CheckBox cash = (CheckBox) findViewById(R.id.cbCreateOrderCash);
			if (cash.isChecked())
				o.params |= ParamState.ofCash;
			else
				o.params &= (~ParamState.ofCash);

			EditText remark = (EditText) findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();

			remark = (EditText) findViewById(R.id.edPayRemark);
			o.payRemark = remark.getText().toString();

			o.retail = (((CheckBox) findViewById(R.id.cbRetail)).isChecked()) ? 1 : 0;

			Spinner spPayTypes = (Spinner)findViewById(R.id.spPayType);
			Object pt = spPayTypes.getSelectedItem();
			if( pt != null )
				o.payType = pt.toString();
			
			if (updateDiscount)
				((OrderImplEx)order).updateDiscount(discount);
			else {
				o.discount = discount;
				order.write();
			}

			if (!editMode)
				Warehouse.open(CreateOrder.this, order, false);

			finish();
		}

		private void askToApplyChanges(Context context) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Внимание");
			builder.setMessage("Изменился тип цены и/или скидка, пересчитать заказ?");

			builder.setPositiveButton("Пересчитать",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							okDone(true);
						}
					});

			builder.setNegativeButton("Оставить",
					new DialogInterface.OnClickListener() {
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			deleteEmptyOrder();
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
