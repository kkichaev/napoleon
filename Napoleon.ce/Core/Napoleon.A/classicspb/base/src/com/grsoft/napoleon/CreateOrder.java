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
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DogovorClassic;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBase;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplClassic;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private boolean editMode = false;
	
	DateHandler dateHandler;
	int selDog = -1;
	
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
//		OrderBase ob = (OrderBase) order.getData();
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(org.name);

		if( !editMode ) 
			initOrder(o, oi.getData());

		ConfigImpl config = new ConfigImpl();
		
//		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
//		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFirma, o.supplyer);
//
		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		DialogHelper.loadSpinnerFromConfig(config, "Склады", new ArrayList<CharSequence>(), spWh, o.getWhName());

//		if( (o.params & ParamState.ofCash) != 0 )
//			((CheckBox)findViewById(R.id.cbCash)).setChecked(true);
//		
		final EditText ed = (EditText)findViewById(R.id.edSumDlv);
		ed.setText(Util.IntToScaleStr(o.sumDlv, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.setEnabled(o.sumDlvFlag > 0);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbDlvFlag); 
		cb.setChecked(o.sumDlvFlag > 0);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton arg0, boolean arg1) { 
				ed.setEnabled(arg1);
				if(arg1) {
					ed.selectAll();
					ed.requestFocus();
				}
			}
		});
		
		List<DogovorClassic> dogovors = loadDogovors(org, o.dogovor);
		Spinner spDog = (Spinner)findViewById(R.id.spDog);
		ArrayAdapter<DogovorClassic> aa = new ArrayAdapter<DogovorClassic>(this, R.layout.simple_spinner_layout, dogovors);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spDog.setAdapter(aa);
		if( selDog >= 0)
			spDog.setSelection(selDog);
		
		Spinner spBonus = (Spinner)findViewById(R.id.spBonus);
		DialogHelper.loadSpinnerFromConfig(config, "Бонус", new ArrayList<CharSequence>(), spBonus, o.bonusAdd);
		
		config.close();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}

	private List<DogovorClassic> loadDogovors(Org org, final String dogovor) {
		selDog = -1;
		String ido = ((OrgBase)org).getIDO();
		final List<DogovorClassic> ret = new ArrayList<DogovorClassic>();
		ret.add(new DogovorClassic());
		DataTraveler.travel(DogovorClassic.class, new DataTraveler.Travel<DogovorClassic>(true) {

			@Override
			public boolean travel(DataTraveler<DogovorClassic> item) {
				if(item.data.id.equals(dogovor))
					selDog = ret.size();
				ret.add(item.data);
				return true;
			}
		}, "idOrg='" + org.id + "' or idOrg='" + ido + "'");
		
		return ret;
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(Order o, Org org) {
		o.sumType = org.costype;
		
		// to next day
		o.date = new Date(Util.getDate().getTime() + 24 * 3600 * 1000);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
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
		if(!editMode) {
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			OrderEx o = (OrderEx)order.getData();
			
			Spinner spDog = (Spinner)findViewById(R.id.spDog);
			DogovorClassic selDog = (DogovorClassic) spDog.getSelectedItem();
			if(selDog == null || selDog.id.length() == 0) {
				Toast.makeText(CreateOrder.this, "Не выбран договор", Toast.LENGTH_SHORT).show();
				return;
			}
			o.dogovor = selDog.id;
//			o.bonus = selDog.bonus;
			
			Spinner spBonus = (Spinner)findViewById(R.id.spBonus);
			CharSequence selb = (CharSequence) spBonus.getSelectedItem();
			if(selb != null)
				o.bonusAdd = selb.toString();
			
			
			
			CheckBox cb = (CheckBox)findViewById(R.id.cbDlvFlag);
			if(cb.isChecked()) {
				EditText ed = (EditText)findViewById(R.id.edSumDlv);
				o.sumDlvFlag = 1;
				o.sumDlv = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
			} else {
				o.sumDlvFlag = 0;
				o.sumDlv = 0;
			}
			
//			OrderBase ob = (OrderBase) order.getData();

//			CheckBox cash = (CheckBox)findViewById(R.id.cbCash);			
//			if( cash.isChecked() ) o.params |= ParamState.ofCash;
//			else o.params &= (~ParamState.ofCash);
//
//			o.bonus = ((CheckBox)findViewById(R.id.cbBonus)).isChecked() ? 1 : 0;
//			if(o.bonus > 0)
//				o.params |= ParamState.ofCash;
			
			Spinner spWh = (Spinner)findViewById(R.id.spWh);
			Object w = spWh.getSelectedItem();
			if(w != null){
				String vl = w.toString();
				if( vl != null && o.getWhName().equals(vl) == false ) {
					o.setWhName(vl);
					o.setWhIndex(spWh.getSelectedItemPosition());
				}
			}

			o.date = dateHandler.getDate();
			
			if (o.created == null)
				o.created = new Date();
			
//			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
//			int suppl = spFirma.getSelectedItemPosition();
//
//			if( suppl >= 0 )
//				o.supplyer = suppl;
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			if (((OrderImplClassic)order).isBonus())
				for (OrderItem i : o.items)
					i.cost = 0;
			
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
