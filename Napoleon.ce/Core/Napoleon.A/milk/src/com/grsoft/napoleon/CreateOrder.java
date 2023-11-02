/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import static com.grsoft.util.Util.IntToStrLeadingZero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;

public class CreateOrder extends Activity
{
	//private OrgImpl org = new OrgImpl();
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	private final String DELAY_STR = "Отсрочка: "; 
	
	private DatePickerDialog.OnDateSetListener dateSetListener;
	private TimePickerDialog.OnTimeSetListener timeSetListener;
	private TextView tvDate;
	private TextView tvTime;
	private TextView tvDelay;
	private Spinner spFirma;
	private Spinner spPrices;
	private boolean editMode = false;
	
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	
	//Date - Time
	private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    
    private final int DEF_DELAY_VALUE = 5;
    
    //Отсрочка
    private int delay = DEF_DELAY_VALUE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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
	
	private void init()
	{
		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvDelay = (TextView) findViewById(R.id.tvDelay);
		spFirma = (Spinner) findViewById(R.id.spFirma);
		spPrices = (Spinner) findViewById(R.id.spPrices);
		
		Button btnOK = (Button) findViewById(R.id.btnOK);
				
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		Calendar calendar = Calendar.getInstance();
		
		initFormConfig();
		
		ArrayAdapter<CharSequence> firmaAdapter = new ArrayAdapter<CharSequence>(this, 
				R.layout.simple_spinner_layout, firms);
		spFirma.setAdapter(firmaAdapter);
		
		ArrayAdapter<CharSequence> pricesAdapter = new ArrayAdapter<CharSequence>(this, 
				R.layout.simple_spinner_layout, priceType);
		spPrices.setAdapter(pricesAdapter);
				
		order.read(orderRowId);
		Order o = order.getData();
		
		if( spPrices.getCount() != o.sumType )
			spPrices.setSelection(o.sumType);
		if( spFirma.getCount() != o.supplyer )
			spFirma.setSelection(o.supplyer);
		
		delay = o.delay;
		tvDelay.setText(Integer.toString(delay));
		
		btnOK.setEnabled(!order.isExported());
		calendar.setTime(o.date);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		if( (o.params & ParamState.ofCash) != 0 ) {
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			cash.setChecked(true);
		}
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		updateDisplayDate();
		
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		updateDisplayTime();
		updateDisplayDelay();
		
		tvDate.setOnClickListener(new DateClickListener());
		dateSetListener = new SetDateListener();
		tvTime.setOnClickListener(new TimeClickListener());
        timeSetListener = new SetTimeListener();
		tvDelay.setOnClickListener(new DelayClickListener());
		btnCancel.setOnClickListener(new CancelClickListener());
		
		btnOK.setOnClickListener(new OKClickListener());
		
		OrgImpl org = new OrgImpl();
		org.getData().id = order.getId();
		org.read();
		tvOrgName.setText(org.getData().name);
	}
	
	private void initFormConfig()
	{
		final char sep = ';';
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "Организация";
		if( config.read() )
			makeList(c.value, sep, firms);

		c.key = "ВидЦены";
		if( config.read() )
			makeList(c.value, sep, priceType);
	}

	private ArrayList<CharSequence> makeList(String value, char sep, ArrayList<CharSequence> list)
	{
		int pos = value.indexOf(sep); 
		
		if (pos == -1 && value.length() > 0)
			list.add(value);
		
		while(pos != -1)
		{
			String f = value.substring(0,pos);
			value = value.substring(pos+1);
			list.add(f);
			pos = value.indexOf(sep); 
			
			if(pos == -1)
			{
				if(value.length() > 0)
					list.add(value);
			}
		}
		
		return list;
	}
	
	private void updateDisplayTime()
	{
		StringBuilder timeText = new StringBuilder();
		
		IntToStrLeadingZero(hour, timeText).append(':');
		IntToStrLeadingZero(minute,timeText);
		
		tvTime.setText(timeText.toString());
	}

	private void updateDisplayDate()
	{
		StringBuilder dateText = new StringBuilder();
		
		IntToStrLeadingZero(day, dateText).append(".");
		IntToStrLeadingZero(month + 1, dateText).append(".");
		IntToStrLeadingZero(year, dateText);
		
		tvDate.setText(dateText.toString());
	}
	
	private Date getDate()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,month,day,hour,minute, 0);
		return calendar.getTime();
	}
	
	private void updateDisplayDelay()
	{
		StringBuilder delayText = new StringBuilder(DELAY_STR);
		delayText.append(delay);
		
		tvDelay.setText(delayText.toString());
	}
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case DIALOG_DATE_PICKER_ID:
				return new DatePickerDialog(this, dateSetListener, year, month, day);
			case DIALOG_TIME_PICKER_ID:
				return new TimePickerDialog(this, timeSetListener, hour, minute, true);
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	
	class DateClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			showDialog(DIALOG_DATE_PICKER_ID);
		}
	}
	
	class SetDateListener implements OnDateSetListener
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth)
		{
			CreateOrder.this.year = year;
            month = monthOfYear;
            day = dayOfMonth;
            updateDisplayDate();
		}
	}
	
	class TimeClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			showDialog(DIALOG_TIME_PICKER_ID);
		}
	}
	
	class SetTimeListener implements OnTimeSetListener
	{

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			hour = hourOfDay;
			CreateOrder.this.minute = minute;
			updateDisplayTime();			
		}
	}
	
	class DelayClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
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
			tvCounter.setText(Integer.toString(delay));
			
			btnCounterUp.setOnClickListener(new OnClickListenerToNotify()
			{
				
				@Override
				public void onClick(View v)
				{
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					++val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterDown.setOnClickListener(new OnClickListenerToNotify()
			{
				@Override
				public void onClick(View v)
				{
					super.onClick(v);
					int val = Integer.parseInt(tvCounter.getText().toString());
					--val;
					tvCounter.setText(Integer.toString(val));
				}
			});
			
			btnCounterOK.setOnClickListener(new OnClickListenerToNotify()
			{
				
				@Override
				public void onClick(View v)
				{
					super.onClick(v);
					delay = Integer.parseInt(tvCounter.getText().toString());
					updateDisplayDelay();
					dialog.hide();
				}
			});
			
			btnCounterCancel.setOnClickListener(new OnClickListenerToNotify()
			{
				
				@Override
				public void onClick(View v)
				{
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
	
	class OKClickListener extends OnClickListenerToNotify
	{
		@Override
		public void onClick(View v)
		{
			super.onClick(v);
			Order o = order.getData();
			o.date = getDate();
			
			if (o.created == null)
				o.created = new Date();
			
			o.sumType = getSumType();
			o.supplyer = getFirmType();
			o.delay = delay;
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);
			o.remark = remark.getText().toString();
			
			order.write();
			
			if(!editMode) Warehouse.open(CreateOrder.this,
					order, false);
			
			finish();
		}

		private int getSumType()
		{
			String priceName = (String)spPrices.getSelectedItem();
			
			int st = priceType.indexOf(priceName);
			return (st < 0) ? 0 : st;
		}
		
		private int getFirmType()
		{
			String firmName = (String)spFirma.getSelectedItem();
			
			int st = firms.indexOf(firmName);
			return (st < 0) ? 0 : st;
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
