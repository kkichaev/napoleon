/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import static com.grsoft.util.Util.IntToStrLeadingZero;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.Order;
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
	
	private DatePickerDialog.OnDateSetListener dateSetListener;
	private TimePickerDialog.OnTimeSetListener timeSetListener;
	private TextView tvDate;
	private TextView tvTime;
	private boolean editMode = false;
	
	
	//Date - Time
	private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    
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
		
		Button btnOK = (Button) findViewById(R.id.btnOK);
				
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		Calendar calendar = Calendar.getInstance();
		
		order.read(orderRowId);
		Order o = order.getData();
				
		btnOK.setEnabled(!order.isExported());
		calendar.setTime(o.date);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		updateDisplayDate();
		
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		updateDisplayTime();
		
		tvDate.setOnClickListener(new DateClickListener());
		dateSetListener = new SetDateListener();
		tvTime.setOnClickListener(new TimeClickListener());
        timeSetListener = new SetTimeListener();
		btnCancel.setOnClickListener(new CancelClickListener());
		
		btnOK.setOnClickListener(new OKClickListener());
		
		OrgImpl org = new OrgImpl();
		org.getData().id = order.getId();
		org.read();
		tvOrgName.setText(org.getData().name);
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
	
	class CancelClickListener extends OnClickListenerToNotify
	{
		@Override
		public void onClick(View v)
		{
			super.onClick(v);
			if(!editMode) {
				if( order.getData().items == null || order.getData().items.size() == 0 )
					order.delete();
			}
				
			finish();
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
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			
			o.remark = remark.getText().toString();
			
			order.write();
			
			if(!editMode) Warehouse.open(CreateOrder.this,
					order, false);
			
			finish();
		}
	}
}
