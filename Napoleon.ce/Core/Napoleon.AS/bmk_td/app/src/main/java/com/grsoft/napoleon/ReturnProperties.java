package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Agreement;
import com.grsoft.dataobjects.OrgAgreement;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.BaseActivity;
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

public class ReturnProperties extends BaseActivity {
	
	private static final int DIALOG_DATE_PICKER_ID = 0;

	ReturnImplEx doc = new ReturnImplEx();
	boolean isOldOrder;
	DateHandler dateHandler;
	
	ArrayList<DeliveryData> deliveries = new ArrayList<DeliveryData>();
	
	public static void open(Context context, ReturnImplEx order, boolean editOldOrder) {
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
		
		ReturnEx o = (ReturnEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);

		loadAgreements(o.agrCode, org);

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
//		int selected = -1;
//		DocList dl = DeliveryDoc.instance().docList(doc.getId(), "date");
//		for(Document<?> ditem : dl) {
//			DeliveryData dd = new DeliveryData((DeliveryImpl) ditem);
//			if( dd.date.equals(o.dlvDate) && dd.number.equals(o.dlvNum)) {
//				selected = deliveries.size();
//			}
//			deliveries.add(dd);
//		}
//		dl.close();
//		ArrayAdapter<DeliveryData> aa = new ArrayAdapter<DeliveryData>(this, R.layout.simple_spinner_layout, deliveries);
		Spinner sp = (Spinner) findViewById(R.id.spDelivery);
		sp.setVisibility(View.GONE);
//		sp.setAdapter(aa);
//		if( selected >= 0 )
//			sp.setSelection(selected);
	
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!doc.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}

	private void loadAgreements(String agrCode, OrgEx org) {
		List<Agreement> data = new ArrayList<>();
		Map<Object, Agreement> agrs = DbReader.fetchDic(Agreement.class, "id");

		int selected = -1;
		for(OrgAgreement oa : org.agreements) {
			Agreement a = agrs.get(oa.id);
			if(a != null) {
				if(agrCode.equals(a.id)) {
					selected = data.size();
				}
				data.add(a);
			}
		}

		if(selected < 0 && data.size() > 0)
			selected = 0;
		Spinner s = findViewById(R.id.spAgr);
		ArrayAdapter<Agreement> aa = new ArrayAdapter<Agreement>(this, R.layout.simple_spinner_layout, data);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( selected >= 0 )
			s.setSelection(selected);
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

			Spinner s = findViewById(R.id.spAgr);
			Agreement agr = (Agreement) s.getSelectedItem();
			if(agr != null) {
				o.agrCode = agr.id;
			}

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();

//			Spinner sp = (Spinner) findViewById(R.id.spDelivery);
//			DeliveryData dd = (DeliveryData) sp.getSelectedItem();
//			if( dd != null ) {
//				o.dlvDate = dd.date;
//				o.dlvNum = dd.number;
//			}

			doc.write();
			finish();
			
			if( isOldOrder == false )
				Warehouse.open(ReturnProperties.this, doc, false);
//				ReturnPriceList.open(ReturnProperties.this, doc);
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
			sum = (int) d.sum();
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
