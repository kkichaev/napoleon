package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.WHouses;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.ExtrasConst;

public class MainPage extends Activity {

	private final static int DIALOG_DATE_PICKER_ID = 1;
	
//	private TextView tvDate;
//	private int year;
//    private int month;
//    private int day;
//	private DatePickerDialog.OnDateSetListener dateSetListener;
	private Spinner spWh;
	
	private ArrayList<CharSequence> payTypes = new ArrayList<CharSequence>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mainpage);
		
		init();
	}
	
	public static class WHousesD extends WHouses{
		@Override public String toString() { return name;	}
	}
	
	int getWhIndex(int whid) {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "WHouse";
		
		String whId = Integer.toString(whid);
		try{
			if(ci.read()) {
				String[] val = c.value.split(";");
				
				for(int i = 0; i < val.length; i++)
					if(whId.equals(val[i])){
						index = i;
						break;
					}
			}
			
			ci.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	private void init() {
//		tvDate = (TextView) findViewById(R.id.tvDate);
		spWh = (Spinner) findViewById(R.id.spWh);
		
		OrderImpl order = CreateOrder.currentOrder();
		if( order == null )
			return;
		
		OrderEx o = (OrderEx)order.getData();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(o.date);
		
//		year = calendar.get(Calendar.YEAR);
//		month = calendar.get(Calendar.MONTH);
//		day = calendar.get(Calendar.DAY_OF_MONTH);
//		updateDisplayDate();
//		tvDate.setOnClickListener(new DateClickListener());
//		dateSetListener = new SetDateListener();
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainPage.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, CreateOrder.currentOrder().getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
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
		
		final ArrayList<Integer> wid = new ArrayList<Integer>();
		
		c.key = "WHouse";
		if( config.read() ) {
			if(c.value.trim().length() > 0){
				try{
					String[] var = c.value.split(";");
					
					for(int i = 0; i < var.length; i++)
						wid.add(Integer.parseInt(var[i]));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		final List<WHousesD> whlist = new ArrayList<WHousesD>();
		
		DataTraveler.travel(WHousesD.class, new DataTraveler.Travel<WHousesD>() {

			@Override
			public boolean travel(DataTraveler<WHousesD> item) {
				if(wid.contains(item.data.id))
					whlist.add(item.data);
				item.data = new WHousesD();
				return true;
			}}, null);
		
		ArrayAdapter<WHousesD> aa = new ArrayAdapter<WHousesD>(this, R.layout.pay_layout, whlist);
		spWh.setAdapter(aa);
		
		int idx = getWhIndex(((OrderEx)order.getData()).whIndex);
		
		if(idx >= 0 && idx < aa.getCount())
			spWh.setSelection(idx, true);
		
		EditText et;
		et = (EditText) findViewById(R.id.edLogText);
		et.setText(o.logistic);

		et = (EditText) findViewById(R.id.edFinText);
		et.setText(o.fcontrol);
	}

//	private void updateDisplayDate()
//	{
//		StringBuilder dateText = new StringBuilder();
//		
//		IntToStrLeadingZero(day, dateText).append(".");
//		IntToStrLeadingZero(month + 1, dateText).append(".");
//		IntToStrLeadingZero(year, dateText);
//		
//		tvDate.setText(dateText.toString());
//	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			if( requestCode == DIALOG_DATE_PICKER_ID ) {
				Date curDate = new Date();
				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				CreateOrder.currentOrder().getData().date = new Date(ct);
				refreshDate();
			}
		}
	}
	

	void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(CreateOrder.currentOrder().getDate()));		
	}
	
//	private Date getDate() {
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year,month,day, 0, 0, 0);
//		return calendar.getTime();
//	}

	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
//		o.date = getDate();
		
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
		
		WHouses w = (WHouses) spWh.getSelectedItem();
		
		if(w != null)
			o.whIndex = w.id;
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch(id) {
//			case DIALOG_DATE_PICKER_ID:
//				return new DatePickerDialog(this, dateSetListener, year, month, day);
//		}
//		
//		return super.onCreateDialog(id);
//	}

//	class DateClickListener implements OnClickListener {
//		@Override
//		public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
//	}
//	
//	class SetDateListener implements OnDateSetListener {
//		@Override
//		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//			MainPage.this.year = year;
//            month = monthOfYear;
//            day = dayOfMonth;
//            updateDisplayDate();
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			CreateOrder.checkOrder();
		return super.onKeyDown(keyCode, event);
	}
}
