/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PackItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RowData;
import com.grsoft.dataobjects.impl.SkladImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.modules.CostHelper;
import com.grsoft.napoleon.modules.CostManager.CostType;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;

public class CreateOrder extends Activity {
	
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private final String DELAY_STR = "Отсрочка: "; 
	
	private boolean editMode = false;
	
	ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	ArrayList<CharSequence> costTypes = new ArrayList<CharSequence>();
	ArrayList<PackItem> units = new ArrayList<PackItem>();
	
	DataObjectAdapter<Firm> firmAdapter;
	DataObjectAdapter<Sklad> skladAdapter;
	    
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
		Order o = order.getData();

		OrgImpl org = new OrgImpl();
		Org orgData = org.getData();
		orgData.id = order.getId();
		org.read();
		org.close();
		
		if( !editMode )
			o.delay = ((OrgEx)orgData).payDelay;
		
		firmAdapter = new DataObjectAdapter<Firm>(this, new FirmImpl());
		skladAdapter = new DataObjectAdapter<Sklad>(this, new SkladImpl());
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
		//findViewById(R.id.tvDelay).setOnClickListener(new DelayClickListener());
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
		});
		findViewById(R.id.tvTime).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_TIME_PICKER_ID); }
		});
				
		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());
		
		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		tvOrgName.setText(orgData.name);
	}
	
	private void updateDisplayDelay() {
		StringBuilder delayText = new StringBuilder(DELAY_STR);
		delayText.append(order.getData().delay);					
		((TextView)findViewById(R.id.tvDelay)).setText(delayText.toString());
	}
	
	private void initAdapter(int resid, final DataObjectAdapter<?> adapter, String id,
			String targetName){
		try{
			final Field target = order.getData().getClass().getField(targetName);
			Spinner spinner = (Spinner) findViewById(resid);
			spinner.setAdapter(adapter);
			int selPos = adapter.getItemPos(id);
			
			if(selPos != -1)
				spinner.setSelection(selPos);
			
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					try{
						target.set(order.getData(), 
								((RowData)adapter.getItem(arg2)).getCode());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void initSpinners(Org org) {
		OrderEx o = (OrderEx) order.getData();
		initAdapter(R.id.spFirma, firmAdapter, o.supplCode, "supplCode");
		initAdapter(R.id.spSklad, skladAdapter, o.whCode, "whCode");
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		CostHelper.loadCostTypes(spPrices, o.costType, new CostHelper.CostSelector() {
			
			@Override
			public void selectedCost(CostType costType, int index) {
				OrderEx oe = (OrderEx) order.getData();
				oe.costType = costType.id;
				oe.sumType = index;
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(firmAdapter != null)
			firmAdapter.close();
		
		if(skladAdapter != null)
			skladAdapter.close();
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
			Order o = order.getData();
			
			if (o.created == null)
				o.created = new Date();
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);
			o.remark = remark.getText().toString();
			
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


class DataObjectAdapter<T extends DataObject> implements SpinnerAdapter{
	private Cursor<?> cursor;
	private Context context;
	
	public DataObjectAdapter(Context context, DbObject<T> dbObject){
		cursor = new Cursor<T>(dbObject);
		this.context = context; 
	}
	
	public int getItemPos(String id) {
		int result = -1;
		
		if (id.trim().length() > 0)
			for(int i = 0; i < getCount(); i++){
				DbObject<?> dbObject = (DbObject<?>) getItem(i);
				
				if (dbObject != null && 
						dbObject instanceof RowData &&
						((RowData)dbObject).checkid(id)){
					result = i;
					break;
				}
			}
		
		return result;
	}

	public void close(){
		cursor.close();
	}
	
	public void refresh(){
		cursor.updateIds();
	}
	
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		return cursor.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return getDropDownView(arg0, arg1, arg2);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		DbObject<?> dbObject = (DbObject<?>) getItem(position);
		if(dbObject != null && dbObject instanceof RowData){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tvFirmaName.setText(((RowData)dbObject).getCaption());
		}
			
		return convertView;
	}
}

