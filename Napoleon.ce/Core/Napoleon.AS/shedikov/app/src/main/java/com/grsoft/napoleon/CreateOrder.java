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
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPda;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Forvarder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.ForvarderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.modules.CostManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateOrder extends Activity {
	
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();

	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private final String DELAY_STR = "Отсрочка: "; 
	
	private boolean editMode = false;
	
	ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	ArrayList<CostTypeEx> costTypes = new ArrayList<CostTypeEx>();
	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	ArrayList<Forvarder> forvarders = new ArrayList<Forvarder>();
	    
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

		OrgImpl org = new OrgImpl();
		OrgEx orgData = (OrgEx)org.getData();
		orgData.id = order.getId();
		org.read();
		org.close();
		
		if( !editMode )
			initOrder(orgData);
		
		initSpinners(orgData);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		if( (o.params & ParamState.ofCash) != 0 ) {
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			cash.setChecked(true);
		}
		
		updateDisplayDate();
		updateDisplayTime();
		updateDisplayDelay();
		
		findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
		findViewById(R.id.tvDelay).setOnClickListener(new DelayClickListener());
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
		});
		findViewById(R.id.tvTime).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_TIME_PICKER_ID); }
		});
				
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());
		
		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		tvOrgName.setText(orgData.name);

		ArrayList<KeyValue> vagents = new ArrayList<KeyValue>();
		Spinner spAgents = (Spinner) findViewById(R.id.spAgents);
		AgentPda a = new AgentPda();
		String table = DataObjectInfo.getInstance().getTableName(AgentPda.class);
		DbReader ar = new DbReader();
		int selected = -1;
		boolean bdo = ar.select(a, table, "", "name");
		while(bdo) {
			if( (o.agent.length() == 0 && a.id.equals(a.userid)) || (o.agent.length() > 0) && a.id.equals(o.agent) )
				selected = vagents.size();
			vagents.add(new KeyValue(a.id, a.name));
			bdo = ar.selectNext(a);
		}
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, vagents);
		spAgents.setAdapter(aa);
		if( selected >= 0 && selected < spAgents.getCount())
			spAgents.setSelection(selected);
	}
	
	private void updateDisplayDelay() {
		StringBuilder delayText = new StringBuilder(DELAY_STR);
		delayText.append(order.getData().delay);					
		((TextView)findViewById(R.id.tvDelay)).setText(delayText.toString());
	}

	private void initOrder(OrgEx org) {
		OrderEx ord = (OrderEx)order.getData();
		
		ord.delay = 5;

		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
		if( ctypes != null ) {
			int sumIndex = 0;
			if( org.costType != null && org.costType.length() > 0 )
				sumIndex = Features.COST_MANAGER.getCostIndex(org.costType);
			
			if( sumIndex < 0 )
				sumIndex = 0;
			ord.sumType = sumIndex;
			ord.costType = ctypes[sumIndex].id;
		}

		if( org.units != null && org.units.size() == 1 )
			ord.unitCode = org.units.get(0).id;
	}

	private void initSpinners(OrgEx org) {
		OrderEx o = (OrderEx)order.getData();
		
		initFirms(o);
		initCostTypes(o);
		initUnits(o, org);
		initForvarders(o, org);
	}

	private void initForvarders(OrderEx o, OrgEx org) {
		Cursor<Forvarder> fc = new Cursor<Forvarder>(new ForvarderImpl());
		
		while(fc.moveNext())
			forvarders.add((Forvarder) fc.current().getData().clone());
		
		fc.close();
		
		int selected = -1;
		
		String fid = o.forvarder.length() > 0 ? o.forvarder : org.forvarder;
		
		if (fid.length() > 0){
			for(int i = 0; i < forvarders.size(); i++)
				if (forvarders.get(i).id.equals(fid)){
					selected = i;
					break;
				}
		}
		
		ArrayAdapter<Forvarder> adapter = new ArrayAdapter<Forvarder>(this,
				R.layout.simple_spinner_layout, forvarders);		
		Spinner s = (Spinner)findViewById(R.id.spForvarder);
		s.setAdapter(adapter);
		
		if( selected >= 0 )
			s.setSelection(selected);

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				Forvarder f = forvarders.get(pos);
				OrderEx ord = (OrderEx)order.getData();
				ord.forvarder = f.id;
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	private void initUnits(OrderEx o, OrgEx org) {
		if( org.units == null )
			return;
		
		int selected = -1;
		for(UnitItem ui : org.units ) {
			if( ui.id.compareTo(o.unitCode) == 0 )
				selected = units.size();

			units.add(new UnitEx(ui));
		}

		if (units.size() > 1){
			units.add(0, new UnitEx(new UnitItem()));

			if (selected >= 0) selected += 1;
		}

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);		
		Spinner s = (Spinner)findViewById(R.id.spUnits);
		s.setAdapter(adapter);
		if( selected >= 0 )
			s.setSelection(selected);

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				UnitEx ut = units.get(pos);
				OrderEx ord = (OrderEx)order.getData();
				ord.unitCode = ut.id;
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	private void initCostTypes(OrderEx o) {
		CostTypeEx selected = null;

		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
		if( ctypes != null ) {
			int index = 0;
			for( CostManager.CostType ct : ctypes ) {
				CostTypeEx ctx = new CostTypeEx(index++, ct);
				costTypes.add(ctx);
				if( ct.id.compareTo(o.costType) == 0 )
					selected = ctx;
			}
		}

		Collections.sort(costTypes, new Comparator<CostTypeEx>() {
			@Override public int compare(CostTypeEx object1, CostTypeEx object2) { return object1.name.compareTo(object2.name); }
		});

		ArrayAdapter<CostTypeEx> adapter = new ArrayAdapter<CostTypeEx>(this, R.layout.simple_spinner_layout, costTypes);		
		Spinner s = (Spinner)findViewById(R.id.spPrices);
		s.setAdapter(adapter);
		if( selected != null ) {
			int selIndex = costTypes.indexOf(selected);
			s.setSelection(selIndex);
		}
		
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				CostTypeEx ct = costTypes.get(pos);
				
				OrderEx ord = (OrderEx)order.getData();
				ord.costType = ct.id;
				ord.sumType = ct.costIndex;
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	private void initFirms(OrderEx o) {
		final char sep = ';';
		
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "Организация";
		if( config.read() ) {
			makeList(c.value, sep, firms);

			ArrayAdapter<CharSequence> firmaAdapter = new ArrayAdapter<CharSequence>(this,  R.layout.simple_spinner_layout, firms);
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			spFirma.setAdapter(firmaAdapter);

			if( spFirma.getCount() > o.supplyer )
				spFirma.setSelection(o.supplyer);

			spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { 
					order.getData().supplyer = position; 
				}
				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
	}

	private ArrayList<CharSequence> makeList(String value, char sep, ArrayList<CharSequence> list) {
		
		int pos = value.indexOf(sep); 
		
		if (pos == -1 && value.length() > 0)
			list.add(value);
		
		while(pos != -1) {
			String f = value.substring(0,pos);
			value = value.substring(pos+1);
			list.add(f);
			pos = value.indexOf(sep); 
			
			if(pos == -1) {
				if(value.length() > 0)
					list.add(value);
			}
		}
		
		return list;
	}
	
	private void updateDisplayTime() {
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		TextView tvDate = (TextView) findViewById(R.id.tvTime);
		tvDate.setText(sf.format(order.getDate()));
	}

	private void updateDisplayDate() {
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		TextView tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setText(sf.format(order.getDate()));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID: {
				Calendar c = Calendar.getInstance();
				c.setTime(order.getDate());
				return new DatePickerDialog(this, new SetDateListener(), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
			case DIALOG_TIME_PICKER_ID: {
				Calendar c = Calendar.getInstance();
				c.setTime(order.getDate());
				return new TimePickerDialog(this, new SetTimeListener(), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
			}
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	class SetDateListener implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar c1 = Calendar.getInstance();
			Calendar c = Calendar.getInstance();

			c1.setTime(order.getDate());
			c.set(year, monthOfYear, dayOfMonth, c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), 0);
			order.getData().date = c.getTime();
			
            updateDisplayDate();
		}
	}
	
	class SetTimeListener implements OnTimeSetListener {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar c1 = Calendar.getInstance();
			Calendar c = Calendar.getInstance();

			c1.setTime(order.getDate());
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
			order.getData().date = c.getTime();

			updateDisplayTime();			
		}
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
			
			btnCounterUp.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					++val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterDown.setOnClickListener(new OnClickListenerToNotify() {
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					--val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterOK.setOnClickListener(new OnClickListenerToNotify() {
				
				@Override
				public void onClick(View v) {
					super.onClick(v);
					int delay = Integer.parseInt(tvCounter.getText().toString());
					order.getData().delay = delay;

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
			OrderEx o = (OrderEx) order.getData();

			if (o.unitCode == null || o.unitCode.trim().length() == 0)
				return;
			
			if (o.created == null)
				o.created = new Date();
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);
			o.remark = remark.getText().toString();
			
			Spinner spAgents = (Spinner) findViewById(R.id.spAgents);
			KeyValue vl = (KeyValue)spAgents.getSelectedItem();
			if( vl != null )
				o.agent = vl.key.toString();
			
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
		}
		
		return true;
	}
}
