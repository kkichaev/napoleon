package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.grsoft.ads.dataobjects.impl.UserOrderImpl;
import com.grsoft.util.ExtrasConst;

public class UserOrderEdit extends Activity {
	protected UserOrderImpl userOrderImpl = new UserOrderImpl();
	private TextView tvDate;
	private SimpleDateFormat sdf;
	private SimpleDateFormat stf;
	private TextView tvTime;
	private long rowid = ExtrasConst.INVALID_ID;
	private EditText edRemark;
	private LinearLayout llDate;
	private LinearLayout llTime;
	private EditText edNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		final LinearLayout llDateSelect = (LinearLayout)findViewById(R.id.llDateSelect);
		llDateSelect.setVisibility(View.GONE);
		
		final LinearLayout llTimeSelect = (LinearLayout)findViewById(R.id.llTimeSelect);
		llTimeSelect.setVisibility(View.GONE);
		
		llDate = (LinearLayout) findViewById(R.id.llDate); 
		llDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				llDateSelect.setVisibility(llDateSelect.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
		});
		
		llTime = (LinearLayout) findViewById(R.id.llTime);
		llTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				llTimeSelect.setVisibility(llTimeSelect.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
		});
		
		rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		edRemark = (EditText) findViewById(R.id.edRemark);
		edNumber = (EditText)findViewById(R.id.edNumber);
	}

	public int getLayoutId() {
		return R.layout.user_order_edit;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		updateDoc();
		userOrderImpl.write();
		userOrderImpl.close();
	}

	protected void updateDoc() {
		userOrderImpl.getData().remark = 
				((TextView)findViewById(R.id.edRemark)).getText().toString();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (rowid != ExtrasConst.INVALID_ID)
		{
			userOrderImpl.read(rowid, false);
			userOrderImpl.close();
			
			sdf = new SimpleDateFormat("dd.MM.yyyy");
			tvDate = (TextView)findViewById(R.id.tvDate);
			tvDate.setText(sdf.format(userOrderImpl.getData().date));
			
			stf = new SimpleDateFormat("HH:mm");
			tvTime = (TextView)findViewById(R.id.tvTime);
			tvTime.setText(stf.format(userOrderImpl.getData().date));

			edRemark.setText(userOrderImpl.getData().remark);
			
			Date date = userOrderImpl.getData().date;
			DatePicker dpDate = (DatePicker) findViewById(R.id.dpDate);
			dpDate.init(date.getYear() + 1900, date.getMonth(), date.getDate(), new OnDateChangedListener() {
				
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					userOrderImpl.getData().date.setYear(year - 1900);
					userOrderImpl.getData().date.setMonth(monthOfYear);
					userOrderImpl.getData().date.setDate(dayOfMonth);
					
					tvDate.setText(sdf.format(userOrderImpl.getData().date));
				}
			});
			
			TimePicker tpTime = (TimePicker) findViewById(R.id.tpTime);
			tpTime.setCurrentHour(date.getHours());
			tpTime.setCurrentMinute(date.getMinutes());
			tpTime.setOnTimeChangedListener(new OnTimeChangedListener() {
				
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					userOrderImpl.getData().date.setHours(hourOfDay);
					userOrderImpl.getData().date.setMinutes(minute);
					
					tvTime.setText(stf.format(userOrderImpl.getData().date));
				}
			});
		}

		edNumber.setText(userOrderImpl.getData().number);
	}
	
	protected void updateControl() {
		if (!userOrderImpl.isEditable()){
			llDate.setOnClickListener(null);
			llTime.setOnClickListener(null);
			edRemark.setEnabled(false);
			edNumber.setEnabled(false);
		}
	}
}
