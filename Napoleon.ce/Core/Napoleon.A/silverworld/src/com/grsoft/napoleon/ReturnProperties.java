package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.BaseActivity;

public class ReturnProperties extends BaseActivity {
	
	private static final int DIALOG_DATE_PICKER_ID = 0;

	ReturnImpl doc = new ReturnImpl();
	boolean isOldOrder;
	DateHandler dateHandler;
	
	ArrayList<DeliveryData> deliveries = new ArrayList<DeliveryData>();
	
	public static void open(Context context, ReturnImpl order, boolean editOldOrder) {
		Intent i = new Intent(context, ReturnProperties.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.return_properties);

		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		isOldOrder = b.getBoolean(ExtrasConst.EDIT_MODE_STR);		
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		doc.read(rid);
		
		ReturnEx o = (ReturnEx)doc.getData();
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);
	
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
		int selected = -1;
		DocList dl = DeliveryDoc.instance().docList(doc.getId(), "date");
		for(Document<?> ditem : dl) {
			DeliveryData dd = new DeliveryData((DeliveryImpl) ditem);
			if( dd.date.equals(o.dlvDate) && dd.number.equals(o.dlvNum)) {
				selected = deliveries.size();
			}
			deliveries.add(dd);
		}
		dl.close();
		ArrayAdapter<DeliveryData> aa = new ArrayAdapter<DeliveryData>(this, R.layout.simple_spinner_layoutex, deliveries);
		Spinner sp = (Spinner) findViewById(R.id.spDelivery);
		sp.setAdapter(aa);
		if( selected >= 0 )
			sp.setSelection(selected);
	
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!doc.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, isOldOrder);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyOrder();			
			finish();
		}
	}
	
	private void deleteEmptyOrder() {
		if(!isOldOrder) {
			if( doc.getData().items == null || doc.getData().items.size() == 0 )
				doc.delete();
		}
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}

	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);

			ReturnEx o = (ReturnEx) doc.getData();
			o.date = dateHandler.getDate();

			if (o.created == null)
				o.created = new Date();

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();

			Spinner sp = (Spinner) findViewById(R.id.spDelivery);
			DeliveryData dd = (DeliveryData) sp.getSelectedItem();
			if( dd != null ) {
				o.dlvDate = dd.date;
				o.dlvNum = dd.number;
			}

			doc.write();
			finish();
			
			if( isOldOrder == false )
				ReturnPriceList.open(ReturnProperties.this, doc);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			deleteEmptyOrder();
			finish();
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
	
	class DeliveryData {
		public String number;
		public int sum;
		public Date date;
		
		public DeliveryData(DeliveryImpl d) {
			date = d.getDate();
			sum = d.sum();
			number = d.getData().number;
		}
		
		@Override
		public String toString() {
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
			return number + " от " + sd.format(date) + " " + 
				Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
	}
}
