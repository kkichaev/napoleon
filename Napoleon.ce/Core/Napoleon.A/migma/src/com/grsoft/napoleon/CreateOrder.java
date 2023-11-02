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
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.InputNumberHelper;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DATE_PICKER_DLG = 0;
	private static final int TIME_PICKER_DLG = 1;
	private static final int REMARK_DLG = 3;
	
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
	DateHandler dateHandler;
	TimeHandler timeHandler;

	private EditText remark;
	
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
		OrderEx o = (OrderEx)order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		if( !editMode ) 
			initOrder(o, (OrgEx)oi.getData());

        ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

		Spinner spWh = (Spinner) findViewById(R.id.spSklad);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, o.whCode);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);

		config.getData().key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
		
		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
		tvDelay.setOnClickListener(new DiscountClickListener());
		
		remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		if(remark != null){
			remark.setInputType(InputType.TYPE_NULL);
			remark.setText(o.remark);
			remark.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDialog(REMARK_DLG);
				}
			});
		}

		((CheckBox)findViewById(R.id.cbCreateOrderCash))
			.setChecked((o.params & ParamState.ofCash) != ParamState.ofCash);
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DATE_PICKER_DLG);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, TIME_PICKER_DLG);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDiscount();
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		if( org.dcost.length() == 0 )
			return;
		
		int sumType = Integer.parseInt(org.dcost.substring(0,1));
		if( sumType > 0 )
			sumType--;
		o.sumType = sumType;

		String[] s = org.dcost.substring(1).split("=");
		if( s.length > 0 )
			o.discount = Util.StrToScale(s[0], Consts.DISCOUNT_SCALE);
		
		if( s.length > 1 && s[1].equals("Н"))
			o.params |= ParamState.ofCash;
	}
	
	private void updateDisplayDiscount() {
		((TextView)findViewById(R.id.tvDelay)).setText("скидка: " + 
				Util.IntToScaleStr(((OrderEx)order.getData()).discount, Consts.DISCOUNT_SCALE,
						Util.DEC_DELIM, false) + " %");
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case REMARK_DLG: prepareRemarkDlg(dialog);
						 break;
		}
	}
	
	private void prepareRemarkDlg(Dialog dialog) {
		EditText edText = (EditText)dialog.findViewById(R.id.edText);
		if(edText != null && remark != null)
			edText.setText(remark.getText().toString());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DATE_PICKER_DLG:
				return dateHandler.createDialog();
			case TIME_PICKER_DLG:
				return timeHandler.createDialog();
			case REMARK_DLG:
				return createRemarkDlg();
		}
		return super.onCreateDialog(id);
	}
	
	private Dialog createRemarkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Введите комментарий к заявке");
		builder.setView(View.inflate(this, R.layout.input_message, null));
		builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialog != null && remark != null){
					TextView edText = (EditText)((AlertDialog)dialog).findViewById(R.id.edText);
					
					if(edText != null){
						String text = edText.getText().toString();
						
						if(text.trim().length() > 0)
							remark.setText(text);
						
						InputMethodManager imm = (InputMethodManager)getSystemService(
							      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
					}
				}
				
			}
		});
		
		builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialog != null){
					TextView edText = (EditText)((AlertDialog)dialog).findViewById(R.id.edText);
					
					if(edText != null){
						InputMethodManager imm = (InputMethodManager)getSystemService(
							      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
					}
				}
			}
		});
		
		return builder.create();
	}

	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	void enterDiscount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Введите скидку");
		final View panel = View.inflate(this, R.layout.inputdiscountdlg, null);
		
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		edCount.setText(Util.IntToScaleStr(((OrderEx)order.getData()).discount, Consts.DISCOUNT_SCALE, Util.DEC_DELIM, false));
		
		InputNumberHelper nh = new InputNumberHelper((EditText)panel.findViewById(R.id.edCount));
		nh.makeNumericKeypad(panel);
		
		ImageButton btnDel = (ImageButton) panel.findViewById(R.id.btnDel);
		btnDel.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				edCount.setText("");
				return false;
			}
		});

		ImageButton btnMinus = (ImageButton) panel.findViewById(R.id.btnMinus);
		btnMinus.setOnClickListener(new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View v) {
				String text = edCount.getText().toString();
				int start = 0;
				if(text.startsWith("-")) text = text.substring(1);
				else {
					text = "-" + text;
					start++;
				}
				edCount.setText(text);
				edCount.setSelection(start, text.length());
			}
		});
		
		builder.setView(panel);
		final AlertDialog dialog = builder.create();
				
		ImageButton btnOK = (ImageButton) panel.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				EditText edCount = (EditText) panel.findViewById(R.id.edCount);
				try{
					int value = Util.StrToScale(edCount.getText().toString(), Consts.DISCOUNT_SCALE);
					((OrderEx)order.getData()).discount = value;					
					updateDisplayDiscount();
					dialog.dismiss();
				}
				catch(Exception e){
					edCount.selectAll();
					String message = e.getMessage();
					if( message == null )
						message = "Проверьте правильность ввода числа";
					MessageBox.show(CreateOrder.this, "Ошибка", message);
				}
				
			}
		});
		
		dialog.show();
	}	
	class DiscountClickListener implements OnClickListener {
		@Override public void onClick(View v) { enterDiscount(); }
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
			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
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
			
			String flag = "Н";
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) {
				flag = "Б";
				o.params &= ~ParamState.ofCash;
			}
			else
				o.params |= ParamState.ofCash;

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			Spinner spWh = (Spinner) findViewById(R.id.spSklad);
			KeyValue  kv = (KeyValue)spWh.getSelectedItem();
			if( kv != null ) {
				o.whCode = kv.key.toString();
				o.whIndex = spWh.getSelectedItemPosition();
			}

			int discount = o.discount;
			String sign = "+";
			if( discount < 0 ) {
				discount = -discount;
				sign = "-";
			}
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy=HH:mm", Locale.getDefault());
			
			o.costtype = String.format("%d%s%02d.%d=%s=%s", o.sumType+1, sign, discount / Consts.DISCOUNT_SCALE, 
					discount % Consts.DISCOUNT_SCALE, flag, sd.format(o.date));

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
