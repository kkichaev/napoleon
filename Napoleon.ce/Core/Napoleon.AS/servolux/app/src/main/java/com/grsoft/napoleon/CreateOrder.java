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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
//	private static final int DIALOG_DATE_PICKER_ID = 0;
//	private static final int DLV_DATE_PICKER_ID = 1;
	//private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
//	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
//	List<OrgDog> dogs = new ArrayList<OrgDog>();
		
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
	
	static HashSet<String> loadAvailFirms(String id, final List<OrgDog> dogovors) {
		HashSet<String> availFirms = new HashSet<String>();

		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = id;
		oi.read();
		oi.close();

		if(dogovors != null)
			dogovors.clear();
		
		OrgDog dog = new OrgDog();
		String table = DataObjectInfo.getInstance().getTableName(dog.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(dog, table, "ido='" + o.ido + "'");
		while(bdo) {
			availFirms.add(dog.firm);
			if(dogovors != null) {
				dogovors.add(dog);
				dog = new OrgDog();
			}
			bdo = r.selectNext(dog);
		}
		r.close();
		
		return availFirms;
	}
	
	static int sel = 0;
	static int loadConfigFirms(ConfigImpl config, final ArrayList<KeyValue> values, final String selected, final HashSet<String> availFirms) {
		sel = -1;
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<Firm>() {

			@Override
			public boolean travel(DataTraveler<Firm> item) {
				KeyValue kv = new KeyValue(item.data.id, item.data.name);
				if( availFirms == null || availFirms.contains(kv.key.toString()) ) {
					if( selected != null && kv.key.equals(selected))
						sel = values.size();
					values.add(kv);
				}
				return true;
			}
		}, "");
				
		return sel;
	}
	
	public static void loadFirms(ConfigImpl config, Spinner spinner, String selected, String id, List<OrgDog> dogovors) {
		HashSet<String> availFirms = loadAvailFirms(id, dogovors);
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();

		int sel = loadConfigFirms(config, values, selected, availFirms);
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(spinner.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spinner.setAdapter(aa);
		if( sel >= 0 && sel < spinner.getCount())
			spinner.setSelection(sel);		
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

		Org org = (Org) oi.getData();		
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) 
			initOrder(o, org);

//		ConfigImpl config = new ConfigImpl();
//		
//		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
//		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				TextView tv = (TextView)findViewById(R.id.tvDogovor);
//				KeyValue kv = (KeyValue)arg0.getAdapter().getItem(arg2);
//				for(OrgDog od : dogs) {
//					if( od.firm.equals(kv.key.toString())) {
//						tv.setText(od.name);
//						return;
//					}
//				}
//				tv.setText("");
//			}
//
//			@Override public void onNothingSelected(AdapterView<?> arg0) { }
//		});
//		loadFirms(config, spFirma, o.firmCode, o.id, dogs);
//		
//		if( !editMode && spFirma.getCount() > 0) {
//			spFirma.setSelection(0);
//			o.supplyer = 0;
//			o.firmCode = ((KeyValue)spFirma.getSelectedItem()).key.toString();
//			order.write();
//		}
//
//		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//		DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);

//		config.getData().key = "МожноИзменятьЦену";
//		try {
//			if (config.read() && Integer.parseInt(config.getData().value) == 0)
//				spPrices.setEnabled(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		config.close();
		
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
		
//		TextView tvDelay = (TextView) findViewById(R.id.tvDelay); 
//		tvDelay.setOnClickListener(new DelayClickListener());
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		if( (o.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCreateOrderCash)).setChecked(true);
		
//		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
//				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
//				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
//			}
//		});
//		
//		findViewById(R.id.tvDlvDate).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
//				i.putExtra(ExtrasConst.DATE_TAG, ((OrderEx)order.getData()).dlvDate.getTime());
//				startActivityForResult(i, DLV_DATE_PICKER_ID);
//			}
//		});
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());
//		btnOK.setEnabled(spFirma.getCount() > 0);

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
//        updateDisplayDelay();
		refreshDate();
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if( data != null ) {
//			if( requestCode == DIALOG_DATE_PICKER_ID ) {
//				Date curDate = new Date();
//				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
//				order.getData().date = new Date(ct);
//				refreshDate();
//			} else if( requestCode == DLV_DATE_PICKER_ID ) {
//				Date curDate = new Date();
//				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
//				((OrderEx)order.getData()).dlvDate = new Date(ct);
//				refreshDate();
//				
//			}
//		}
//	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
		((TextView)findViewById(R.id.tvDlvDate)).setText(sd.format(((OrderEx)order.getData()).dlvDate));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, Org org) {
		o.sumType = org.costype;
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
			okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			OrderEx o = (OrderEx)order.getData();
			
			if (o.created == null)
				o.created = new Date();
			
//			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
//			int suppl = spFirma.getSelectedItemPosition();
//			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//			int costType = spPrices.getSelectedItemPosition();
//
//			if( suppl >= 0 ) {
//				o.supplyer = suppl;
//				o.firmCode = ((KeyValue)spFirma.getSelectedItem()).key.toString();
//			}
//			if( costType >= 0 )
//				o.sumType = costType;
//			
//			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);			
//			if( cash.isChecked() ) o.params |= ParamState.ofCash;
//			else o.params &= (~ParamState.ofCash);

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			order.setExported(false);
			order.write();
			
			if(!editMode)
				order.open(CreateOrder.this);
			
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
