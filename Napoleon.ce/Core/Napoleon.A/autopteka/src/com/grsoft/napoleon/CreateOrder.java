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
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
//import com.grsoft.napoleon.modules.CostManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

public class CreateOrder extends BaseActivity
{
	protected static final int SET_ORDER_DATE = 1;

	boolean editMode;
	private OrderImpl order;
	
	class DogovorEx extends Dogovor {
		public DogovorEx(Dogovor d) {
			number = d.number;
			name = d.name;
			from = d.from;
			till = d.till;
//			costType = d.costType;
		}
		@Override public String toString() { return name; }
	}
	
//	class CostTypeEx extends CostManager.CostType {
//		
//		int costIndex;
//		
//		public CostTypeEx(int index, CostManager.CostType c) { 
//			super(c.id, c.name);
//			costIndex = index;
//		}
//		
//		@Override public String toString() { return name ; }
//	}
	
	ArrayList<DogovorEx> dogovors = new ArrayList<DogovorEx>();
//	ArrayList<CostTypeEx> costTypes = new ArrayList<CostTypeEx>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);

		long rowId;
		if( savedInstanceState == null ) {
			editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
			rowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		} else {
			editMode = savedInstanceState.getBoolean(ExtrasConst.EDIT_MODE_STR);
			rowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		}
		order = new OrderImpl();
		order.read(rowId);
		
		OrgImpl org = new OrgImpl();
		OrgEx o = (OrgEx)org.getData();
		o.id = order.getId();		
		org.read();
		
		((TextView)findViewById(R.id.tvOrgName)).setText(o.name);
		
		// init order fields
		if( !editMode )
			initOrder(o);
		
		setData(o);
		org.close();
		
		((Button)findViewById(R.id.btnCancel)).setOnClickListener(new CancelClickListener());
		((Button)findViewById(R.id.btnOK)).setOnClickListener(new OKClickListener());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SET_ORDER_DATE ) {
			Calendar c = Calendar.getInstance();
			c.setTime(order.getDate());
			
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Calendar c = Calendar.getInstance();
					c.set(year, monthOfYear, dayOfMonth);
					order.getData().date = c.getTime();
					refreshOrderDate();
				}
			} ,c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		}
		return super.onCreateDialog(id);
	}

	private void setData(OrgEx o) {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setOnClickListener(new View.OnClickListener() {			
			@Override 
			public void onClick(View v) { showDialog(SET_ORDER_DATE); }
		});
		
		EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(order.getData().remark);
		
		loadDogovors(o);
//		loadCostTypes();
		refreshOrderDate();
	}
	
//	private void loadCostTypes() {
//		CostTypeEx selected = null;
//		OrderEx ord = (OrderEx)order.getData();
//
//		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
//		if( ctypes != null ) {
//			int index = 0;
//			for( CostManager.CostType ct : ctypes ) {
//				CostTypeEx ctx = new CostTypeEx(index++, ct);
//				costTypes.add(ctx);
//				if( ct.id.compareTo(ord.sumTypeID) == 0 )
//					selected = ctx;
//			}
//		}
//
//		Collections.sort(costTypes, new Comparator<CostTypeEx>() {
//			@Override public int compare(CostTypeEx object1, CostTypeEx object2) { return object1.name.compareTo(object2.name); }
//		});
//
//		ArrayAdapter<CostTypeEx> adapter = new ArrayAdapter<CostTypeEx>(this, R.layout.simple_spinner_layout, costTypes);		
//		Spinner s = (Spinner)findViewById(R.id.spPrices);
//		s.setAdapter(adapter);
//		if( selected != null ) {
//			int selIndex = costTypes.indexOf(selected);
//			s.setSelection(selIndex, true);
//		}
//		
//		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//					onCostChanged(costTypes.get(pos));
//			}
//			@Override public void onNothingSelected(AdapterView<?> arg0) {}
//		});
//	}

	private void loadDogovors(OrgEx o) {
		int selIndex = -1;
		
		OrderEx ord = (OrderEx)order.getData();
		if( o.dogovors != null ) {
			for(Dogovor d : o.dogovors) {
				dogovors.add(new DogovorEx(d));
				
				if( d.number.compareTo(ord.dogNum) == 0 )
					selIndex = dogovors.size() - 1;
			}
		}
		
		ArrayAdapter<DogovorEx> adapter = new ArrayAdapter<DogovorEx>(this, R.layout.simple_spinner_layout, dogovors);
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		s.setAdapter(adapter);
		if( selIndex >= 0 )
			s.setSelection(selIndex);
		
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			private int lastPos = -1;
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(lastPos != -1 && lastPos != pos)
					onDogovorChanged(dogovors.get(pos));
				lastPos = pos;
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	private void onDogovorChanged(DogovorEx d) {
		((OrderEx)order.getData()).dogNum = d.number;
//		
//		if( d.costType.length() > 0 ) {
//			int index = 0;
//			for(CostTypeEx ct : costTypes) {
//				if( ct.id.compareTo(d.costType) == 0) {
//					Spinner s = (Spinner)findViewById(R.id.spPrices);
//					s.setSelection(index);
//					break;
//				}
//				index++;
//			}
//		}
	}
	
//	private void onCostChanged(CostTypeEx ct) {
//		OrderEx ord = (OrderEx)order.getData();
//		ord.sumTypeID = ct.id;
//		ord.sumType = ct.costIndex;
//	}
//
	private void refreshOrderDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		tv.setText(sf.format(order.getDate()));
	}

	private void updateOrder(OrderImpl order) {
		EditText et = (EditText)findViewById(R.id.edRemark);		
		order.getData().remark = et.getText().toString();
		order.write();
	}

	private void initOrder(OrgEx o) {
		OrderEx ord = (OrderEx)order.getData();
					
//		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
//		if( ctypes != null ) {
//			ord.sumType = 0;
//			ord.sumTypeID = ctypes[0].id;
//		}
//
		if( o.dogovors != null && o.dogovors.size() > 0 ) {
			Dogovor dog = o.dogovors.get(0);
			ord.dogNum = dog.number;
//			if( dog.costType.length() > 0 ) {
//				ord.sumTypeID = dog.costType;
//				ord.sumType = Features.COST_MANAGER.getCostIndex(dog.costType);
//			}
		}
	}
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, editMode);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
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
			
			updateOrder(order);
			
			super.onClick(v);
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
		}
		
		return true;
	}
}
