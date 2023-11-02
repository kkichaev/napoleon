package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.GlobalServiceContext;

public class ConfigurationEx extends Configuration {
	EditText edCounterGen;
	EditText edCounterDop;
	EditText edReceiptCnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		edCounterGen = new EditText(this);
		edCounterGen.setHint(R.string.gen_dog_counter);
		edCounterDop = new EditText(this);
		edCounterDop.setHint(R.string.extr_dog_counter);
		edReceiptCnt = new EditText(this);
		edReceiptCnt.setHint(R.string.receipt_counter);
		
		super.onCreate(savedInstanceState);

		LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayout01);
//		TextView tv = new TextView(this);
//		tv.setText(R.string.gen_dog_counter);
//		ll.addView(tv);
//		ll.addView(edCounterGen);
		TextView tv = new TextView(this);
		tv.setText(R.string.extr_dog_counter);
		ll.addView(tv);
		ll.addView(edCounterDop);
		tv = new TextView(this);
		tv.setText(R.string.receipt_counter);
		ll.addView(tv);
		ll.addView(edReceiptCnt);
	}

	@Override
	protected void init() {
		super.init();

		SharedPreferences pref = GlobalServiceContext.service.getSharedPreferences(SalesImplEx.SALESIMPLPREF, Context.MODE_PRIVATE);
		edCounterGen.setText(Integer.toString(pref.getInt(SalesImplEx.COUNTERGEN, 1)));
		edCounterDop.setText(Integer.toString(pref.getInt(SalesImplEx.COUNTERDOP, 1)));
		edReceiptCnt.setText(Integer.toString(pref.getInt(SalesImplEx.RECEIPTCNT, 1)));
	}

	@Override
	public void save() {
		super.save();

		try {
			SharedPreferences pref = GlobalServiceContext.service
					.getSharedPreferences(SalesImplEx.SALESIMPLPREF,
							Context.MODE_PRIVATE);
			Editor ed = pref.edit();
			ed.putInt(SalesImplEx.COUNTERGEN, Integer.parseInt(edCounterGen.getText().toString()));
			ed.putInt(SalesImplEx.COUNTERDOP, Integer.parseInt(edCounterDop.getText().toString()));
			ed.putInt(SalesImplEx.RECEIPTCNT, Integer.parseInt(edReceiptCnt.getText().toString()));
			ed.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
