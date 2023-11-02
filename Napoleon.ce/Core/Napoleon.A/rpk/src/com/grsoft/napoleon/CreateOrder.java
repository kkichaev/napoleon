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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;

public class CreateOrder extends Activity
{
	static final int Quality = 4;
	static final int Sert = 8;
	
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
	private boolean editMode = false;
	
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
		
		Button btnOK = (Button) findViewById(R.id.btnOK);
				
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		Calendar calendar = Calendar.getInstance();
		
		order.read(orderRowId);
		OrderEx o = (OrderEx)order.getData();
		
		OrgImpl org = new OrgImpl();
		org.getData().id = order.getId();
		org.read();
		OrgEx oe = (OrgEx)org.getData();

		if( editMode == false ) {
			o.delay = oe.payDelay;
			o.discount = oe.discount;
		}
		delay = o.delay;
		tvDelay.setText(Integer.toString(delay));
		
		btnOK.setEnabled(!order.isExported());
		calendar.setTime(o.date);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		if( (o.params & Quality) != 0 ) {
			CheckBox cb = (CheckBox)findViewById(R.id.cbQuality);
			cb.setChecked(true);
		}
		
		if( (o.params & Sert) != 0 ) {
			CheckBox cb = (CheckBox)findViewById(R.id.cbSert);
			cb.setChecked(true);
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
		btnCancel.setOnClickListener(new CancelClickListener());
		
		btnOK.setOnClickListener(new OKClickListener());
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
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			CheckBox cb = (CheckBox)findViewById(R.id.cbQuality);			
			if( cb.isChecked() ) o.params |= Quality;
			else o.params &= (~Quality);
			
			cb = (CheckBox)findViewById(R.id.cbSert);			
			if( cb.isChecked() ) o.params |= Sert;
			else o.params &= (~Sert);

			o.remark = remark.getText().toString();
			
			order.write();
			
			boolean gotoWarehouse = true;
			
			if(!editMode){
				long rmnid = RemnantsImpl.find(o.id, Util.getDateTime());
				
				if(rmnid != ExtrasConst.INVALID_ID){
					OrgImpl org = new OrgImpl();
					org.getData().id = order.getId();
					org.read();
					
					if(org.read()){
						OrgEx oe = (OrgEx)org.getData();
						
					if(oe.remnants != null && oe.remnants.size() > 0){
							OrderMaster.open(v.getContext(), rmnid, order.getRowid());
							gotoWarehouse = false;
						}
					}
					
					org.close();
				}
				
				if(gotoWarehouse)
					Warehouse.open(CreateOrder.this,order, false);
			}
					
			
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
