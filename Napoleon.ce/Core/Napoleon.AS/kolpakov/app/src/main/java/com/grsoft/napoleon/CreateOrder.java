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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	String clientid;
	boolean canSaveWithoutDogovor = false;
	
	private ArrayList<Sklads> sklads = new ArrayList<Sklads>();
	List<PriceType> prcItems = null;
	
	int selItem = -1;
	int inited = 0;
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
		final OrderEx o = (OrderEx) order.getData();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);
		clientid = oe.clientid;

		loadSklads(o);
				
		if( !editMode ) 
			initOrder(o, oe);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		com.grsoft.dataobjects.Config cfg = config.getData(); 
		cfg.key = "МожноИзменятьЦену";
		try {
			if (config.read() && Integer.parseInt(cfg.value) == 0)
				spPrices.setEnabled(false);
			cfg.key = "МожноСоздаватьБезДоговора";
			if( config.read() && Integer.parseInt(cfg.value) != 0 )
				canSaveWithoutDogovor = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		config.close();
		
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
//					ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
//					spAddress.setAdapter(aa);
//					if( selected >= 0 && selected < spAddress.getCount())
//						spAddress.setSelection(selected);
//				}
//			}
//		}
		
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
		
        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
		refreshDate();
	}

	private void loadSklads(final OrderEx o) {
		selItem = -1;
		
		DataTraveler.travel(Sklads.class, new DataTraveler.Travel<Sklads>(){
			@Override
			public boolean travel(DataTraveler<Sklads> item) {
				if( !editMode ) {
					if( item.data.def != 0 ) {
						o.storeid = item.data.id;
						selItem = sklads.size();
					}
				} else {
					if( o.storeid.equals(item.data.id))
						selItem = sklads.size();
				}
				sklads.add(item.data);
				item.data = new Sklads();
				return true;
			}
			
		}, "");
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		ArrayAdapter<Sklads> aa = new ArrayAdapter<Sklads>(this, R.layout.simple_spinner_layout, sklads);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spFirma.setAdapter(aa);
		if( selItem >= 0 && selItem < sklads.size())
			spFirma.setSelection(selItem);
		
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( (inited & 1)  == 0)
					inited |= 1;
				else
					refreshDogovors(true); 
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	
		refreshDogovors(!editMode);
	}


	protected void refreshDogovors(final boolean updateOrder) {
		selItem = -1;
		final OrderEx o = (OrderEx)order.getData();
		
		Sklads sel = (Sklads)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( sel == null )
			return;
		
		final List<Dogovor> dogovors = new ArrayList<Dogovor>();
		DataTraveler.travel(Dogovor.class, new DataTraveler.Travel<Dogovor>(){
			@Override
			public boolean travel(DataTraveler<Dogovor> item) {
				if( updateOrder ) {
					if( item.data.def != 0 ) {
						o.contractid = item.data.id;
						selItem = dogovors.size();
					}
				} else {
					if( o.contractid.equals(item.data.id))
						selItem = dogovors.size();
				}
				dogovors.add(item.data);
				item.data = new Dogovor();
				return true;
			}
			
		}, "clientid='" + clientid + "' and companyid='" + sel.idOrg + "'");
		
		Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
		ArrayAdapter<Dogovor> aa = new ArrayAdapter<Dogovor>(this, R.layout.simple_spinner_layout, dogovors);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spDog.setAdapter(aa);
		
		if( selItem >= 0 && selItem < dogovors.size())
			spDog.setSelection(selItem);
		
		((TextView)findViewById(R.id.tvDogInfo)).setText(Html.fromHtml(""));
		
		if( dogovors.size() == 0 && updateOrder )
			o.contractid = "";

		findViewById(R.id.btnOK).setEnabled(order.isEditable() && (dogovors.size() > 0 || canSaveWithoutDogovor));

		spDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { 
				Dogovor selDog =(Dogovor)(arg0.getAdapter().getItem(arg2)); 
				String di = selDog == null ? "" : selDog.info;
				((TextView)findViewById(R.id.tvDogInfo)).setText(Html.fromHtml(di));

				if( (inited & 2)  == 0)
					inited |= 2;
				else {
					refreshCost(true);
				}
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	
		refreshCost(!editMode);
	}

	protected void refreshCost(boolean updateOrder) {

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		final OrderEx o = (OrderEx)order.getData();

		String selid = "";
		if( updateOrder ) {
			final Dogovor sel = (Dogovor)((Spinner) findViewById(R.id.spDogovor)).getSelectedItem();
			if( sel != null ) 
				selid = sel.priceid;

			if( selid.length() == 0 ) {
				Sklads sels = (Sklads)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
				if( sels != null )
					selid = sels.priceid;
			}
		} else
			selid = o.priceid;

		if( prcItems == null ) {
			prcItems = new ArrayList<PriceType>();
			DataTraveler.travel(PriceType.class, new DataTraveler.Travel<PriceType>() {

				@Override
				public boolean travel(DataTraveler<PriceType> item) {
					prcItems.add(item.data);
					item.data = new PriceType();
					return true;
				}
			}, "");

			ArrayAdapter<PriceType> aprc = new ArrayAdapter<PriceType>(this, R.layout.simple_spinner_layout, prcItems);
			aprc.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			spPrices.setAdapter(aprc);
		}

		Adapter a = spPrices.getAdapter();
		for( int i=0; i<a.getCount(); i++) {
			PriceType pt = (PriceType)a.getItem(i);
			if( pt != null ) {
				if( pt.id.equals(selid) )  {
					spPrices.setSelection(i);
					break;
				}
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
	private void initOrder(OrderEx o, OrgEx org) {
		o.sumType = org.costype;
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
			PriceType pt = (PriceType) spPrices.getSelectedItem();
			OrderEx oe = (OrderEx) order.getData();
			
			if (oe.items.size() > 0 && pt != null && oe.priceid.equals(pt.id) == false)
				askToApplyNewSumType(v.getContext());
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
			PriceType pt = (PriceType) spPrices.getSelectedItem();

			if( suppl >= 0 ) {
				o.supplyer = suppl;
				Sklads skl = (Sklads)spFirma.getSelectedItem();
				o.storeid = skl.id;
				o.companyid = skl.idOrg;
				o.whIndex = skl.index;
			}
			
			if( pt != null )
				o.priceid = pt.id;
			else
				o.priceid = "";
			
			Spinner spDog = (Spinner)findViewById(R.id.spDogovor);
			Dogovor selDg = (Dogovor) spDog.getSelectedItem();
			if(selDg != null)
				o.contractid = selDg.id;
			else
				o.companyid = "";
			
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
//			if( Features.DELIVERY_ADDRESS ) {
//				Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
//				if( spAddress != null ) {
//					KeyValue sel = (KeyValue) spAddress.getSelectedItem();
//					if( sel != null )
//						o.adrCode = sel.key.toString();
//				}
//			}
			if (updateSumType)
				order.updateItemsCost(o.sumType);
			else
				order.write();
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
		
		private void askToApplyNewSumType(Context context){
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
