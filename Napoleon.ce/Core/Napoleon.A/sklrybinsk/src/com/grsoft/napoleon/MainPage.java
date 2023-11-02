package com.grsoft.napoleon;

import static com.grsoft.util.Util.IntToStrLeadingZero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;

public class MainPage extends Activity {

	private final static int DIALOG_DATE_PICKER_ID = 1;
	
	private TextView tvDate;
	private int year;
    private int month;
    private int day;
	private DatePickerDialog.OnDateSetListener dateSetListener;

	private ArrayList<CharSequence> payTypes = new ArrayList<CharSequence>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mainpage);
		
		init();
	}
	
	private void init() {
		tvDate = (TextView) findViewById(R.id.tvDate);
		
		OrderImpl order = CreateOrder.currentOrder();
		if( order == null )
			return;
		
		OrderEx o = (OrderEx)order.getData();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(o.date);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		updateDisplayDate();
		tvDate.setOnClickListener(new DateClickListener());
		dateSetListener = new SetDateListener();
		
	    payTypes.add("Отсрочка");
	    payTypes.add("Нал/Факт");

	    ArrayAdapter<CharSequence> ptAdapter = new ArrayAdapter<CharSequence>(this, R.layout.pay_layout, payTypes);
		Spinner spPayType = (Spinner) findViewById(R.id.spPayType);
		spPayType.setAdapter(ptAdapter);
		spPayType.setSelection( ((o.params & OrderEx.ofFact) != 0) ? 1 : 0 );
	
		CheckBox payBefore = (CheckBox) findViewById(R.id.cbCreateOrderCash);
		payBefore.setChecked((o.params & OrderEx.ofPayBefore) != 0);
		
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		
		c.key = "ФинКонтроль";
		if( config.read() ) {
			TextView pp = (TextView) findViewById(R.id.tvFinPhone);
			pp.setText(c.value);
		}

		c.key = "Логист";
		if( config.read() ) {
			TextView lp = (TextView) findViewById(R.id.tvLogPhone);
			lp.setText(c.value);
		}
		
		EditText et;
		et = (EditText) findViewById(R.id.edLogText);
		et.setText(o.logistic);

		et = (EditText) findViewById(R.id.edFinText);
		et.setText(o.fcontrol);
	}

	private void updateDisplayDate()
	{
		StringBuilder dateText = new StringBuilder();
		
		IntToStrLeadingZero(day, dateText).append(".");
		IntToStrLeadingZero(month + 1, dateText).append(".");
		IntToStrLeadingZero(year, dateText);
		
		tvDate.setText(dateText.toString());
	}

	private Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,month,day, 0, 0, 0);
		return calendar.getTime();
	}

	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		o.date = getDate();
		
		EditText et;
		et = (EditText) findViewById(R.id.edLogText);
		o.logistic = et.getText().toString();

		et = (EditText) findViewById(R.id.edFinText);
		o.fcontrol = et.getText().toString();

	    Spinner spPayType;
		spPayType = (Spinner) findViewById(R.id.spPayType);
		if( spPayType.getSelectedItemPosition() == 0) o.params &= (~OrderEx.ofFact);
		else o.params |= OrderEx.ofFact;
		
		CheckBox payBefore = (CheckBox) findViewById(R.id.cbCreateOrderCash);
		if( payBefore.isChecked() ) o.params |= OrderEx.ofPayBefore;
		else o.params &= (~OrderEx.ofPayBefore);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return new DatePickerDialog(this, dateSetListener, year, month, day);
		}
		
		return super.onCreateDialog(id);
	}

	class DateClickListener implements OnClickListener {
		@Override
		public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
	}
	
	class SetDateListener implements OnDateSetListener {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			MainPage.this.year = year;
            month = monthOfYear;
            day = dayOfMonth;
            updateDisplayDate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			CreateOrder.checkOrder();
		return super.onKeyDown(keyCode, event);
	}
}
