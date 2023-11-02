package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.dataobjects.MoneyProxy;
import com.grsoft.dataobjects.impl.MoneyProxyImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MoneyProxyDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;

public class MoneyProxyForm extends Activity {

	private static final int DIALOG_DATE_PICKER_ID = 0;

	private MoneyProxyImpl doc;
	private ImageButton btnSend;
	
	public static void open(Context context, MoneyProxyImpl doc) {
		Intent i = new Intent(context, MoneyProxyForm.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		long docRowId = ExtrasConst.INVALID_ID;
		if( savedInstanceState == null )
			docRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			docRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		doc = (MoneyProxyImpl)MoneyProxyDoc.instance().create();
		doc.read(docRowId);
		
		MoneyProxy d = doc.getData();
		
		View v = View.inflate(this, R.layout.moneyproxy, null);
		setContentView(v);
		
		OrgImpl o = new OrgImpl();
		o.getData().id = d.id;
		o.read();
		
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(o.getData().name);
		
		updateDate();
		
		tv = (TextView)findViewById(R.id.tvDate);
		tv.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) { showDialog(DIALOG_DATE_PICKER_ID); }
		});
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(d.remark);
		
		InputNumberHelper nh = new InputNumberHelper((EditText)v.findViewById(R.id.edCount));
		nh.makeNumericKeypad(v);
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				
				if( doc.isEditable() )
					saveDocument();
				
				new DocumentSender(MoneyProxyForm.this, btnSend, 
						MoneyProxyDoc.OBJ_NAME, doc, doc.getRowid()).execute((Void[])null);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
			{
				Calendar c = Calendar.getInstance();
				c.setTime(doc.getDate());

				return new DatePickerDialog(this, 
						new DatePickerDialog.OnDateSetListener() {
							
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								Calendar ci = Calendar.getInstance();
								ci.setTime(doc.getDate());
								ci.set(year, monthOfYear, dayOfMonth, ci.get(Calendar.HOUR_OF_DAY), 
										ci.get(Calendar.MINUTE),  ci.get(Calendar.SECOND));
								
								doc.getData().date = ci.getTime();
								updateDate();
							}},
						c.get(Calendar.YEAR),
						c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH));
			}
		}
		return super.onCreateDialog(id);
	}

	private void updateDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		tv.setText(sf.format(doc.getDate()));
	}
	
	void saveDocument() {
		MoneyProxy d = doc.getData();
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		d.sum = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
		
		ed = (EditText)findViewById(R.id.edRemark);
		d.remark = ed.getText().toString();
		
		if( d.sum == 0 && (d.remark == null || d.remark.length() == 0))
			doc.delete();
		else
			doc.write();
			
		doc.close();
		
		MoneyProxyDoc.instance().refreshDocSum(doc.getId());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if( doc.isEditable() )
			saveDocument();
	}
}
