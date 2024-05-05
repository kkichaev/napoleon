/*
 * Copyright (C), 2010, √ильди€ –азработчиков
 *
 * —оздать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Delivery;
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
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	OrgEx org;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
//	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	
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
		org = (OrgEx) oi.getData();
		org.id = o.id;
		oi.read();
		oi.close();

		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) {
			initOrder(o, org);
			showDialog(R.id.debet_info_dialog);
		}
		
		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "ќрганизаци€", firms, spFirma, o.supplyer);
		DialogHelper.loadSpinnerFromConfig(config, "‘ормаќплаты", new ArrayList<CharSequence>(), 
				(Spinner)findViewById(R.id.spPayType), o.payType);

//		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//		DialogHelper.loadSpinnerWithKey(config, "¬ид÷ены", priceType, spPrices, o.sumType);

//		config.getData().key = "ћожно»змен€ть÷ену";
//		try {
//			if (config.read() && Integer.parseInt(config.getData().value) == 0)
//				spPrices.setEnabled(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		config.close();
		

//		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
//		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		EditText remAdr = (EditText)findViewById(R.id.edRemAdr);
		remAdr.setText(o.remAdr);

		EditText remInfo = (EditText)findViewById(R.id.edRemInfo);
		remInfo.setText(o.remInfo);
		
		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
		if( o.vetCert != 0 )
			((CheckBox)findViewById(R.id.cbVetCert)).setChecked(true);
		
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
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

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
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализаци€ дополнительных полей за€вки (индивидуально дл€ проекта)
	 * @param o
	 */
	private void initOrder(Order o, OrgEx org) {
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "¬ид÷ены";
		config.read();
		config.close();
		
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		int selCost = DialogHelper.makeListWithKey(c.value, values, org.prcType);
		o.sumType = selCost;
		o.prcType = org.prcType;
		
		Calendar clnd = Calendar.getInstance();
		clnd.setTime(o.date);
		clnd.add(Calendar.DAY_OF_MONTH, 1);
		o.date = clnd.getTime();
	}
	
	private void updateDisplayDelay() {
		((TextView)findViewById(R.id.tvDelay)).setText("отсрочка: " + 
				order.getData().delay);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.debet_info_dialog) {
			return createDebetInfo();
		}
		
		switch(id) {
//			case DIALOG_DATE_PICKER_ID:
//				return dateHandler.createDialog();
			case DIALOG_TIME_PICKER_ID:
				return timeHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	private Dialog createDebetInfo() {
		long outDebet = 0;

		DbWriter.checkDBTable(Delivery.class);
		if(org.delay > 0) {
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.setTime(Util.getDate());
			c.add(Calendar.DAY_OF_MONTH, -org.delay);
			
			String stmt = "select sum(sumD) from Delivery where id='" + org.id + "' and sumD > 0 and date < " + Long.toString(c.getTime().getTime());
			Cursor cur = null;
			try {
				cur = DataBaseManager.getDataBase().rawQuery(stmt, null);
				if(cur.moveToNext())
					outDebet = cur.getLong(0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if( cur != null )
					cur.close();
			}
		}
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		
		String txt = "Ћимит: " + (org.limit > 0 ? Util.IntToScaleStr(org.limit, Consts.SUM_SCALE, Util.DEC_DELIM, false) : "") + 
				"<br/>острочка: " + (org.delay > 0 ? Integer.toString(org.delay) : "") +
				"<br/>долг: " + Util.IntToScaleStr(org.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				
		if( outDebet > 0) {
			txt += "<br/><b>просрочено: " + Util.IntToScaleStr(outDebet, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		}
		
		b.setMessage(Html.fromHtml(txt));
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
		});
		b.setTitle("»нформаци€");
		return b.create();
	}

	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
//	class DelayClickListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//			builder.setTitle("ќтсрочка");
//			View dialogView = View.inflate(v.getContext(), R.layout.counter, null);
//			
//			builder.setView(dialogView);
//			final AlertDialog dialog = builder.create();
//			
//			Button btnCounterUp = (Button) dialogView.findViewById(R.id.btnCounterUp);
//			Button btnCounterDown = (Button) dialogView.findViewById(R.id.btnCounterDown);
//			Button btnCounterOK = (Button) dialogView.findViewById(R.id.btnCounterOk);
//			Button btnCounterCancel = (Button) dialogView.findViewById(R.id.btnCounterCancel);
//			final  TextView  tvCounter = (TextView) dialogView.findViewById(R.id.edCounter);
//			tvCounter.setText(Integer.toString(order.getData().delay));
//			tvCounter.setFocusable(false);
//			
//			btnCounterUp.setOnClickListener(new OnClickListenerToNotify() {
//				
//				@Override
//				public void onClick(View v) {
//					super.onClick(v);
//					int val = Integer.parseInt(tvCounter.getText().toString());
//					++val;
//					tvCounter.setText(Integer.toString(val));
//				}
//			});
//			
//			btnCounterDown.setOnClickListener(new OnClickListenerToNotify() {
//				@Override
//				public void onClick(View v) {
//					super.onClick(v);
//					int val = Integer.parseInt(tvCounter.getText().toString());
//					
//					if (val > 0)
//						--val;
//					
//					tvCounter.setText(Integer.toString(val));
//				}
//			});
//			
//			btnCounterOK.setOnClickListener(new OnClickListenerToNotify() {
//				
//				@Override
//				public void onClick(View v) {
//					super.onClick(v);
//					order.getData().delay = Integer.parseInt(tvCounter.getText().toString());
//					updateDisplayDelay();
//					dialog.hide();
//				}
//			});
//			
//			btnCounterCancel.setOnClickListener(new OnClickListenerToNotify() {
//				
//				@Override
//				public void onClick(View v) {
//					super.onClick(v);
//					dialog.hide();
//				}
//			});
//		
//			dialog.show();
//		}
//	}
	
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
//			if( costType >= 0 )
//				o.sumType = costType;
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);
			
			o.vetCert = ((CheckBox)findViewById(R.id.cbVetCert)).isChecked() ? 1 : 0;
			String val = (String) ((Spinner)findViewById(R.id.spPayType)).getSelectedItem();
			if( val != null )
				o.payType = val;

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			o.remAdr = ((EditText)findViewById(R.id.edRemAdr)).getText().toString();
			o.remInfo = ((EditText)findViewById(R.id.edRemInfo)).getText().toString();

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
