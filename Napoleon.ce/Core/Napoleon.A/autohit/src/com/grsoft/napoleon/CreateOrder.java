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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgDogovors;
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
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_DLV_DATE = 1;
	boolean dogovorSelected = false;
	List<OrgDogovors> dogovors;
	List<KeyValue> priceTypes = new ArrayList<KeyValue>();

	protected static final int DOGOVOR_INFO = 10;
	
	private boolean editMode = false;
		
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

		OrgEx org = (OrgEx) oi.getData();		
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		final View btnOK = findViewById(R.id.btnOK);

		dogovors = new ArrayList<OrgDogovors>();
		for(OrgDogovors od : org.dogovors)
			dogovors.add(od);

		if( !editMode ) 
			initOrder(o, org);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerWithKey(config, "Организация", new ArrayList<KeyValue>(), spFirma, o.firmCode);
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { loadDogovors(); }
			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		loadDogovors();

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		priceTypes.add(new KeyValue("", ""));
		DialogHelper.loadSpinnerWithKey(config, "ВидЦены", priceTypes, spPrices, o.payType);
		spPrices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyValue selected = (KeyValue)arg0.getAdapter().getItem(arg2);
				btnOK.setEnabled(selected != null && selected.key.toString().length() > 0);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});

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
		((CheckBox)findViewById(R.id.cbBill)).setChecked(o.bill > 0);
		((CheckBox)findViewById(R.id.cbFastDlv)).setChecked(o.fastDlv > 0);
		
		
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
				i.putExtra(ExtrasConst.DATE_TAG, ((OrderEx)order.getData()).dlvDate);
				startActivityForResult(i, DIALOG_DLV_DATE);
			}
		});
		
		findViewById(R.id.btnInfo).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(DOGOVOR_INFO); }
		});
		
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());

		refreshDate();
		
		((Spinner)findViewById(R.id.spDogovor)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				OrgDogovors od = (OrgDogovors)arg0.getAdapter().getItem(arg2);
//				Boolean okEnabled = (od.checkDay != 0 && od.outDays == 0) || (od.checkDay == 0);
//				btnOK.setEnabled(okEnabled);
				
				for(int i = 0; i < priceTypes.size(); i++){
					String id = priceTypes.get(i).key.toString();
					if( id.equals(od.idPay)) {
						((Spinner) findViewById(R.id.spPrices)).setSelection(i, true);
						break;
					}
				}
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	protected void loadDogovors() {
		if( dogovorSelected ) {
			dogovorSelected = false;
			return;
		}
		
		KeyValue firm = (KeyValue)((Spinner)findViewById(R.id.spFirma)).getSelectedItem();
		if( firm != null ) {
			String firmId = firm.key.toString();
			List<OrgDogovors> dogs = new ArrayList<OrgDogovors>();
			for(OrgDogovors od : dogovors)
				if( od.idOrg.equals(firmId) )
					dogs.add(od);
			
			Spinner spDogs = (Spinner)findViewById(R.id.spDogovor);
			ArrayAdapter<OrgDogovors> aa = new ArrayAdapter<OrgDogovors>(this, R.layout.simple_spinner_layout, dogs);
			spDogs.setAdapter(aa);
			findViewById(R.id.btnOK).setEnabled(dogs.size() > 0);
			
			OrderEx oe = (OrderEx) order.getData();
			for(int i = 0; i < aa.getCount(); i++){
				OrgDogovors od = aa.getItem(i);
				
				if(od.id.equals(oe.dogovor)){
					spDogs.setSelection(i, true);
					break;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && (requestCode == DIALOG_DATE_PICKER_ID || requestCode == DIALOG_DLV_DATE) ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			if( requestCode == DIALOG_DATE_PICKER_ID )
				order.getData().date = newDate;
			else
				((OrderEx)order.getData()).dlvDate = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		OrderEx o = (OrderEx) order.getData();
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(o.date));		
		((TextView)findViewById(R.id.tvDlvDate)).setText(sd.format(o.dlvDate));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		o.sumType = org.costype;
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.dlvDate = c.getTime();
		
		for(OrgDogovors od : org.dogovors) {
			if( od.isMain != 0 ) {
				o.dogovor = od.id;
				o.firmCode = od.idOrg;
				o.payType = od.idPay;
				break;
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DOGOVOR_INFO:
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle("Договор");
				b.setMessage("");
				return b.create();
//				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == DOGOVOR_INFO) {
			String message = "";
			OrgDogovors dg = (OrgDogovors)((Spinner)findViewById(R.id.spDogovor)).getSelectedItem();
			if( dg != null ) {
				String firm = "", pay = "";
				ConfigImpl ci = new ConfigImpl();
				Config cfg = ci.getData();
				ArrayList<KeyValue> values = new ArrayList<KeyValue>();
				cfg.key = "Организация";
				ci.read();
				int sel = DialogHelper.makeListWithKey(cfg.value, values, dg.idOrg);
				if( sel  >= 0 )
					firm = values.get(sel).value.toString();

				values.clear();
				cfg.key = "ВидЦены";
				ci.read();
				sel = DialogHelper.makeListWithKey(cfg.value, values, dg.idPay);
				if( sel  >= 0 )
					pay = values.get(sel).value.toString();
				ci.close();

				message += dg.name + "<br>";
				message += "Организация: <b>" + firm + "</b><br>"; 
				message += "Тип цены: <b>" + pay+ "</b><br>"; 
				if( dg.checkPay > 0 ) {
					message += "Лимит/макс.заказ: <b>" + Util.IntToScaleStr(dg.payLimit, Consts.SUM_SCALE) + " / " + 
						Util.IntToScaleStr(dg.maxOrder, Consts.SUM_SCALE)+ "</b><br>";
				}
				
				if( dg.checkDay > 0 ) {
					message += "Лимит отсрочки: <b>" + Util.IntToScaleStr(dg.dayLimit, 1) + "</b><br>";
					if( dg.outDays > 0 ) {
						message += "Просрочено: <b>" + Util.IntToScaleStr(dg.outDays, 1) + "</b><br>";						
					}
				}
			}
			((AlertDialog)dialog).setMessage(Html.fromHtml(message));
		}
		super.onPrepareDialog(id, dialog);
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
			int costType = spPrices.getSelectedItemPosition() - 1;
			
			if (editMode && (order.getSumType() != costType && costType >= 0))
				askToApplyNewSumType(v.getContext(), costType);
			else 
				okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			OrderEx o = (OrderEx) order.getData();
						
			if (o.created == null)
				o.created = new Date();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition() - 1;

			if( suppl >= 0 ) {
				o.supplyer = suppl;
				o.firmCode = ((KeyValue)spFirma.getSelectedItem()).key.toString();
			}
			if( costType >= 0 ) {
				o.sumType = costType;
				o.payType = ((KeyValue)spPrices.getSelectedItem()).key.toString();
			}
			
			boolean waitFinish = false;
			OrgDogovors dg = (OrgDogovors)((Spinner)findViewById(R.id.spDogovor)).getSelectedItem();
			if( dg != null ) {
				o.dogovor = dg.id;
				if(dg.checkDay != 0 && dg.outDays != 0) {
					waitFinish = true;
					
					AlertDialog.Builder b = new AlertDialog.Builder(CreateOrder.this);
					b.setMessage(String.format("Просрочка платежа %d дней", dg.outDays));
					b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { 
							if(!editMode)
								Warehouse.open(CreateOrder.this, order, false);
							finish(); 
						}
					});
					b.create().show();
				}
			}

			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			o.bill = ((CheckBox)findViewById(R.id.cbBill)).isChecked() ? 1 : 0;
			o.fastDlv = ((CheckBox)findViewById(R.id.cbFastDlv)).isChecked() ? 1 : 0;
			
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
			
			if(!waitFinish) {
				if(!editMode)
					Warehouse.open(CreateOrder.this, order, false);
				finish();
			}
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
