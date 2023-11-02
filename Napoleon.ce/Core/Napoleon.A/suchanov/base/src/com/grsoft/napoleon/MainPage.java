package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;

public class MainPage extends Activity {

	private final static int DIALOG_DATE_PICKER_ID = 1;
	private final static int DIALOG_TIME_PICKER_ID = 2;
	private final static int DIALOG_PAY_DATE_PICKER_ID = 3;
	private final static int DIALOG_PAY_TIME_PICKER_ID = 4;
	
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	private ArrayList<CharSequence> banks = new ArrayList<CharSequence>();
	
	private OrderImpl order;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mainpage);
		
		init();
	}
	
	private void init() {
		order = CreateOrder.currentOrder();
		if( order == null )
			return;
		
		OrderEx o = (OrderEx)order.getData();

		initArrays();
		
	    ArrayAdapter<CharSequence> adapter;
		Spinner spinner;
		
		adapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, firms);
		spinner = (Spinner) findViewById(R.id.spFirma);
		spinner.setAdapter(adapter);
		spinner.setSelection(o.supplyer);

		adapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, priceType);
		spinner = (Spinner) findViewById(R.id.spPrices);
		spinner.setAdapter(adapter);
		spinner.setSelection(o.sumType);

		adapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, banks);
		spinner = (Spinner) findViewById(R.id.spBank);
		spinner.setAdapter(adapter);
		spinner.setSelection(o.bank);
		
		if( (o.flags & OrderEx.TOPIC_B) != 0 )
			((CheckBox)findViewById(R.id.cbCash)).setChecked(true);
		if( (o.flags & OrderEx.DISCOUNT) != 0 )
			((CheckBox)findViewById(R.id.cbWDiscount)).setChecked(true);

		updateDate(true);
		updateTime(true);
		updateDate(false);
		updateTime(false);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
		});
		
		findViewById(R.id.tvPayDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_PAY_DATE_PICKER_ID); }
		});

		findViewById(R.id.tvTime).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_TIME_PICKER_ID); }
		});

		findViewById(R.id.tvLimitTime).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_PAY_TIME_PICKER_ID); }
		});
	}

	private void updateTime(boolean orderTime) {
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");		
		Date d = (orderTime) ? order.getDate() : ((OrderEx)order.getData()).pay;
		int id = (orderTime) ? R.id.tvTime : R.id.tvLimitTime;
		((TextView)findViewById(id)).setText(sd.format(d));
	}

	private void updateDate(boolean orderDate) {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		Date d = (orderDate) ? order.getDate() : ((OrderEx)order.getData()).pay;
		int id = (orderDate) ? R.id.tvDate : R.id.tvPayDate;
		((TextView)findViewById(id)).setText(sd.format(d));
	}

	private void initArrays() {
		final char sep = ';';
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "Организация";
		if( config.read() )
			makeList(c.value, sep, firms);

		c.key = "ВидЦены";
		if( config.read() )
			makeList(c.value, sep, priceType);
		
		c.key = "Банк";
		if( config.read() )
			makeList(c.value, sep, banks);

		config.close();
	}

	public static ArrayList<CharSequence> makeList(String value, char sep, ArrayList<CharSequence> list) {
		int pos = value.indexOf(sep); 
		
		if (pos == -1 && value.length() > 0)
			list.add(value);
		
		while(pos != -1) {
			String f = value.substring(0,pos);
			value = value.substring(pos+1);
			list.add(f);
			pos = value.indexOf(sep); 
			
			if(pos == -1 && value.length() > 0)
				list.add(value);
		}
		
		return list;
	}

	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		
		Spinner s;
		s = (Spinner) findViewById(R.id.spFirma);
		o.supplyer = s.getSelectedItemPosition();
		
		s = (Spinner) findViewById(R.id.spPrices);
		o.sumType = s.getSelectedItemPosition();
		
		s = (Spinner) findViewById(R.id.spBank);
		o.bank = s.getSelectedItemPosition();
		
		if(((CheckBox)findViewById(R.id.cbCash)).isChecked())
			o.flags |= OrderEx.TOPIC_B;
		else
			o.flags &= (~OrderEx.TOPIC_B);
		
		if(((CheckBox)findViewById(R.id.cbWDiscount)).isChecked())
			o.flags |= OrderEx.DISCOUNT;
		else
			o.flags &= (~OrderEx.DISCOUNT);
	}
	
	class SetDate implements DatePickerDialog.OnDateSetListener {
		boolean setOrderDate;
		
		public SetDate(boolean setOrderDate) { this.setOrderDate = setOrderDate; }
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
			OrderEx oe = (OrderEx)order.getData();
			
			c1.setTime((setOrderDate) ? oe.date : oe.pay);
			c.set(year, monthOfYear, dayOfMonth, c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), 0);
			
			if(setOrderDate)
				oe.date = c.getTime();
			else
				oe.pay = c.getTime();

			updateDate(setOrderDate);
		}
	}
	
	Dialog makeDateDialog(boolean setOrderDate) {
		Calendar calendar = Calendar.getInstance();
		OrderEx oe = (OrderEx)order.getData();
		calendar.setTime((setOrderDate) ? oe.date : oe.pay);

		return new DatePickerDialog(this, new SetDate(setOrderDate),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	class SetTime implements TimePickerDialog.OnTimeSetListener {
		boolean setOrderTime;
		
		public SetTime(boolean setOrderTime) { this.setOrderTime = setOrderTime; }
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
			OrderEx oe = (OrderEx)order.getData();
			
			c1.setTime((setOrderTime) ? oe.date : oe.pay);
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
			
			if(setOrderTime)
				oe.date = c.getTime();
			else
				oe.pay = c.getTime();
			updateTime(setOrderTime);
		}
	}

	Dialog makeTimeDialog(boolean setOrderTime) {
		Calendar calendar = Calendar.getInstance();
		OrderEx oe = (OrderEx)order.getData();
		calendar.setTime((setOrderTime) ? oe.date : oe.pay);

		return new TimePickerDialog(this, new SetTime(setOrderTime),
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_PAY_DATE_PICKER_ID:
			case DIALOG_DATE_PICKER_ID:
				return makeDateDialog((id == DIALOG_DATE_PICKER_ID));
			case DIALOG_PAY_TIME_PICKER_ID:
			case DIALOG_TIME_PICKER_ID:
				return makeTimeDialog((id == DIALOG_TIME_PICKER_ID));
		}
		
		return super.onCreateDialog(id);
	}
}
