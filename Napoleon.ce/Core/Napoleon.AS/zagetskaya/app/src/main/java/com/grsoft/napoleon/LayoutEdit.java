package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.LayoutItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.LayoutFailureCause;
import com.grsoft.dataobjects.impl.LayoutImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class LayoutEdit extends Activity implements OnClickListener, OnLongClickListener, OnDateSetListener {
	private static final String ITEM_ID = "item_id";
	private static final String DATE_FMT = "dd.MM.yyyy";
	private EditText edQty;
	private EditText edDate;
	private View btnCalendar;
	private LayoutImpl doc = new LayoutImpl();
	private LayoutItem item;
	protected LinearLayout llKeyboard;
	private View btnOK;
	private Spinner spFailureCause;
	private EditText edRemark;
	
	public static void open(Context context, long rowid, String itemid) {
		Intent i = new Intent(context, LayoutEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(ITEM_ID, itemid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layoutedit);
		
		edQty = (EditText) findViewById(R.id.edQty);
		edDate = (EditText) findViewById(R.id.edDate);
		btnCalendar = findViewById(R.id.btnCalendar);
		btnOK = (ImageButton) findViewById(R.id.btnOK);
		spFailureCause = (Spinner) findViewById(R.id.spFailureCause);
		edRemark = (EditText) findViewById(R.id.edRemark);
		
		btnCalendar.setOnClickListener(this);
		btnCalendar.setOnLongClickListener(this);
		btnOK.setOnClickListener(this);
		
		edQty.setInputType(InputType.TYPE_NULL);
		edDate.setInputType(InputType.TYPE_NULL);
		edQty.selectAll();
		edQty.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				edQty.selectAll();
			}
		});
		
		new KeypadHelper(this, R.id.edQty);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		item = doc.findItem(getIntent().getStringExtra(ITEM_ID));
		
		if (item != null) {
			String text = item.qty > 0 ? Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) : "";
			edQty.setText(text);
			edDate.setText(item.date);
			edRemark.setText(item.remark);
		}
		
		final List<String> cause = new ArrayList<String>();
		cause.add("");
		
		DataTraveler.travel(LayoutFailureCause.class, new DataTraveler.Travel<LayoutFailureCause>() {

			@Override
			public boolean travel(DataTraveler<LayoutFailureCause> item) {
				cause.add(item.data.name);
				return true;
			}
		}, null);
		
		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, cause);
		spFailureCause.setAdapter(aa);
		
		if (item != null)
			for(int i = 0; i < spFailureCause.getCount(); i++) {
				if (spFailureCause.getItemAtPosition(i).toString().equals(item.cause)) {
					spFailureCause.setSelection(i, true);
					break;
				}
			}
				
		
		btnOK.setEnabled(doc.isEditable());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK)
			doOK();
		else if (v.getId() == R.id.btnCalendar)
			showDialog(R.id.btnCalendar);
		
	}

	private void doOK() {
		if (item != null) {
			item.qty = (int)Util.StrToScale(edQty.getText().toString().trim(), Consts.QTY_SCALE);
			item.date = edDate.getText().toString().trim();
			
			if (spFailureCause.getSelectedItem() != null) 
				item.cause = spFailureCause.getSelectedItem().toString();
			
			item.remark = edRemark.getText().toString();
			
			doc.write();
			doc.close();
			finish();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.btnCalendar) {
			edDate.setText("");
			return true;
		}
		
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.btnCalendar)
			return datePickerDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog datePickerDlg() {
		try {
			Date d = new Date();
			if (edDate.getText().toString().length() > 0)
				d = new SimpleDateFormat(DATE_FMT).parse(edDate.getText().toString().trim());
			return new DatePickerDialog(this, this, d.getYear() + 1900, d.getMonth(), d.getDate());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.btnCalendar) {
			prepareDateDlg((DatePickerDialog)dialog);
		}else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareDateDlg(DatePickerDialog dialog) {
		try {
			Date d = new Date();
			if (edDate.getText().toString().length() > 0)
				d = new SimpleDateFormat(DATE_FMT).parse(edDate.getText().toString().trim());
			dialog.updateDate(d.getYear() + 1900, d.getMonth(), d.getDate());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		edDate.setText(addZero(dayOfMonth) + "." + addZero(monthOfYear + 1) + "." + year);
	}
	
	private String addZero(int val) {
		String result = Integer.toString(val); 
		if (val < 10)
			result = "0" + result;
		
		
		return result;
			
	}
}
