package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.impl.ManagerAgentImpl;
import com.grsoft.dataobjects.impl.SPKTaskImpl;
import com.grsoft.util.ExtrasConst;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class SPKTaskEdit extends Activity implements OnClickListener {
	private TextView tvStart;
	private TextView tvFinish;
	private TextView tvName;
	private EditText edSkill;
	private EditText edStrengths;
	private EditText edRazvitie;
	private EditText edTask;

	private SPKTaskImpl doc = new SPKTaskImpl();

	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, SPKTaskEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.task_edit);

		tvStart = (TextView) findViewById(R.id.tvStart);
		tvFinish = (TextView) findViewById(R.id.tvFinish);
		tvName = (TextView) findViewById(R.id.tvName);
		edSkill = (EditText) findViewById(R.id.edSkill);
		edStrengths = (EditText) findViewById(R.id.edStrengths);
		edRazvitie = (EditText) findViewById(R.id.edRazvitie);
		edTask = (EditText) findViewById(R.id.edTask);

		tvStart.setOnClickListener(this);
		tvFinish.setOnClickListener(this);

		View v = getLayoutInflater().inflate(getActionBarLayoutID(), null);
		ActionBar a = getActionBar();
		a.setCustomView(v);
		a.setDisplayShowCustomEnabled(true);
		a.setDisplayShowTitleEnabled(false);

		TextView tv = (TextView) v.findViewById(R.id.tvTitle);
		tv.setText(R.string.tasks);

		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();

		setTime(doc.getData().start, tvStart);
		setTime(doc.getData().finish, tvFinish);

		ManagerAgentImpl m = new ManagerAgentImpl();
		m.read("id", doc.getData().agentid);

		tvName.setText(m.getData().name);

		edSkill.setText(doc.getData().skill);
		edStrengths.setText(doc.getData().strengths);
		edRazvitie.setText(doc.getData().razvitie);
		edTask.setText(doc.getData().task);

		edSkill.setEnabled(doc.isEditable());
		edStrengths.setEnabled(doc.isEditable());
		edRazvitie.setEnabled(doc.isEditable());
		edTask.setEnabled(doc.isEditable());
	}

	private int getActionBarLayoutID() {
		return R.layout.action_bar;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (doc.isEditable()) {
			doc.getData().skill = edSkill.getText().toString().trim();
			doc.getData().strengths = edStrengths.getText().toString().trim();
			doc.getData().razvitie = edRazvitie.getText().toString().trim();
			doc.getData().task = edTask.getText().toString().trim();

			doc.write();
			doc.close();
		}
	}

	DatePickerDialog.OnDateSetListener setStart = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			doc.getData().start = new Date(year - 1900, monthOfYear, dayOfMonth);
			setTime(doc.getData().start, tvStart);
		}
	};

	DatePickerDialog.OnDateSetListener setFinish = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			doc.getData().finish = new Date(year - 1900, monthOfYear, dayOfMonth);
			setTime(doc.getData().finish, tvFinish);
		}
	};

	private void showCalendar(Date date, DatePickerDialog.OnDateSetListener result) {
		DatePickerDialog dlg = new DatePickerDialog(this, result, date.getYear() + 1900, date.getMonth(),
				date.getDate());
		dlg.show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tvStart)
			showCalendar(doc.getData().start, setStart);
		else if (v.getId() == R.id.tvFinish)
			showCalendar(doc.getData().finish, setFinish);
	}

	private void setTime(Date date, TextView textView) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
		StringBuilder sb = new StringBuilder();
		sb.append("<u>").append(sdf.format(date)).append("</u>");
		textView.setText(Html.fromHtml(sb.toString()));
	}
}
