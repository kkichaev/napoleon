package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateReturnEx extends CreateReturn {
	@Override
	protected void init() {
		super.init();

		OrgEx oe = (OrgEx) oi.getData();

		if (oe.day2 == 0)
			findViewById(R.id.llDate2).setVisibility(View.GONE);
		else {
			findViewById(R.id.llDate1).setVisibility(View.GONE);
			setDaysControl(oe);
			refreshDate();
		}
	}

	private void setDaysControl(OrgEx org) {
		StringBuilder sb = new StringBuilder();

		Calendar c = Calendar.getInstance();
		c.setTime(Util.resetTime(doc.getDate()));

		final int DATE_COUNT = 2;
		int idx = 0;
		Date[] arr = new Date[DATE_COUNT];

		for (int i = 0; i <= 7 && idx < DATE_COUNT; i++) {
			int dw = c.get(Calendar.DAY_OF_WEEK);

			String dn = "";

			if (dw == Calendar.MONDAY && ((org.day2 & 1) == 1))
				dn = "Ïí";
			else if (dw == Calendar.TUESDAY && ((org.day2 & 2) == 2))
				dn = "Âò";
			else if (dw == Calendar.WEDNESDAY && ((org.day2 & 4) == 4))
				dn = "Ñð";
			else if (dw == Calendar.THURSDAY && ((org.day2 & 8) == 8))
				dn = "×ò";
			else if (dw == Calendar.FRIDAY && ((org.day2 & 16) == 16))
				dn = "Ïò";
			else if (dw == Calendar.SATURDAY && ((org.day2 & 32) == 32))
				dn = "Ñá";
			else if (dw == Calendar.SUNDAY && ((org.day2 & 64) == 64))
				dn = "Âñ";

			if (dn.length() > 0) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(dn);

				arr[idx++] = c.getTime();
			}

			c.add(Calendar.DATE, 1);
		}

		TextView tv = (TextView) findViewById(R.id.tvDays);
		tv.setText(getString(R.string.delivery_days, sb.toString()));

		if (!editMode && arr.length > 0)
			doc.getData().date = arr[0];

		initDateRB(arr[0], (RadioButton) findViewById(R.id.rbOne));
		initDateRB(arr[1], (RadioButton) findViewById(R.id.rbTwo));

		CheckBox cb = (CheckBox) findViewById(R.id.cbCustomDate);
		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(R.id.custom_date_dlg);
			}
		});

		RadioGroup rg = (RadioGroup) findViewById(R.id.rgDays);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				doc.getData().date = (Date) findViewById(checkedId).getTag();
				((ReturnEx) doc.getData()).dateRemark = "";

				refreshDate();

			}
		});
	}

	protected void initDateRB(Date d, RadioButton rb) {
		if (d != null) {
			rb.setText(Util.simpleDateFormat.format(d));
			rb.setTag(d);
			rb.setChecked(doc.getDate().equals(d));
		} else
			rb.setVisibility(View.GONE);
	}

	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));
		((TextView)findViewById(R.id.tvDate2)).setText(sd.format(doc.getDate()));
		
		((CheckBox)findViewById(R.id.cbCustomDate)).setChecked(
				((ReturnEx)doc.getData()).dateRemark.length() > 0);
	}

	@Override
	int getContentViewID() {
		return R.layout.createreturnex;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.custom_date_dlg)
			prepareCustomDateDlg(dialog);
		super.onPrepareDialog(id, dialog);
	}

	private void prepareCustomDateDlg(Dialog dialog) {
		DatePicker dp = (DatePicker) dialog.findViewById(R.id.date);
		Calendar c = Calendar.getInstance();
		c.setTime(doc.getDate());
		dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

		EditText ed = (EditText) dialog.findViewById(R.id.edRemark);
		ed.setText(((ReturnEx) doc.getData()).dateRemark);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.custom_date_dlg)
			return createCustomDateDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createCustomDateDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.custom_date_dlg, null);
		
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText ed = (EditText) ((Dialog)dialog).findViewById(R.id.edRemark);
				
				String rem = ed.getText().toString().trim();
				
				if(rem.length() > 0) {
					DatePicker dp = (DatePicker) ((Dialog)dialog).findViewById(R.id.date);
					
					int day = dp.getDayOfMonth();
				    int month = dp.getMonth();
				    int year =  dp.getYear();
	
				    Calendar calendar = Calendar.getInstance();
				    calendar.set(year, month, day);
				    
					doc.getData().date = calendar.getTime();
					((ReturnEx)doc.getData()).dateRemark = rem;
				}
				
				refreshDate();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				refreshDate();
			}
		});
		
		return builder.create();
	}
}
