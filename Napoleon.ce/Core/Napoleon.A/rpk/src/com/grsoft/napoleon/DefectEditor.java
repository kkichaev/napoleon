package com.grsoft.napoleon;

import static com.grsoft.util.Util.IntToStrLeadingZero;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Defect;
import com.grsoft.dataobjects.impl.DefectImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;

public class DefectEditor extends Activity {

	private static final int DIALOG_DATE_PICKER_ID = 0;
	
	private boolean editMode = false;
	private int year;
    private int month;
    private int day;

    DefectImpl doc = new DefectImpl();

	public static void open(Context ctx, DefectImpl doc, boolean isOldDoc) {
		Intent i = new Intent(ctx, DefectEditor.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, isOldDoc);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		ctx.startActivity(i);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.defect_edit);

		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rowid);

		Defect d = doc.getData();
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();

		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		tvOrgName.setText(org.getData().name);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(d.remark);

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		updateDisplayDate();

		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setEnabled(!doc.isExported());
		btnOK.setOnClickListener(new OKClickListener());
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new CancelClickListener());

		TextView tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
		});
	}
	
	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			if(!editMode && (doc.getData().items == null || doc.getData().items.size() == 0))
				doc.delete();

			finish();
		}
	}
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			Defect d = doc.getData();

			Calendar calendar = Calendar.getInstance();
			calendar.set(year,month,day,0,0,0);
			d.date = calendar.getTime();
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			d.remark = remark.getText().toString();
			
			doc.write();
			
			if( !editMode )
				Warehouse.open(DefectEditor.this, doc, false);
			
			finish();
		}		
	}

	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id)
		{
			case DIALOG_DATE_PICKER_ID:
				return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						DefectEditor.this.year = year;
						month = monthOfYear;
						day = dayOfMonth;
						
						updateDisplayDate();
					}}, year, month, day);
		}
		return super.onCreateDialog(id);
	}

	private void updateDisplayDate() {
		StringBuilder dateText = new StringBuilder();
		
		IntToStrLeadingZero(day, dateText).append(".");
		IntToStrLeadingZero(month + 1, dateText).append(".");
		IntToStrLeadingZero(year, dateText);
		
		TextView tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setText(dateText.toString());
	}
}
