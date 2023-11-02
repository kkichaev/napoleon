package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ReturnProperties extends BaseActivity {
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int ASK_CLEAR_ITEMS = 2;

	ReturnImplEx doc = new ReturnImplEx();
	boolean isOldOrder;
	//DateHandler dateHandler;
	
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
		
		final ReturnEx o = (ReturnEx) doc.getData();
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);
	
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		//dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ReturnProperties.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getData().date.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		DocList dl = DeliveryDoc.instance().docList(doc.getId(), "date");
		for(Document<?> ditem : dl) {
			DeliveryData dd = new DeliveryData((DeliveryImpl) ditem);
			deliveries.add(dd);
		}
		dl.close();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!doc.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        
        ConfigImpl config = new ConfigImpl();
        Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFirma, o.supplyer);
		
		spFirma.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String firm = (String) arg0.getItemAtPosition(arg2);
				
				List<DeliveryData> list = new ArrayList<DeliveryData>();
				
				int selected = -1;
				
				for(DeliveryData d : deliveries) {
					if (d.firma.equals(firm)) {
						if( d.date.equals(o.dlvDate) && d.number.equals(o.dlvNum)) {
							selected = list.size();
						}
						
						list.add(d);
					}
				}
				
				ArrayAdapter<DeliveryData> aa = new ArrayAdapter<DeliveryData>(arg1.getContext(), R.layout.simple_spinner_layout, list);
				Spinner sp = (Spinner) findViewById(R.id.spDelivery);
				sp.setAdapter(aa);
				
				if(selected != -1)
					sp.setSelection(selected, true);
			}
		});
		
		refreshDate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			doc.getData().date = newDate;
			refreshDate();
		}
	}

	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));		
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
			//case DIALOG_DATE_PICKER_ID:
			//	return dateHandler.createDialog();
				
			case ASK_CLEAR_ITEMS: {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle("Предупреждение");
				b.setMessage("Вы пытаетесь изменить фирму, товар в текущем возврате будет удалён. Продолжить?");
				b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
				});
				b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Return r = doc.getData();
						r.supplyer = ((Spinner) findViewById(R.id.spFirma)).getSelectedItemPosition();
						r.items.clear();
						doc.write();
						dialog.dismiss();
						finish();
					}
				});
				return b.create();
			}
				 
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
			//o.date = dateHandler.getDate();

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

			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int pos = spFirma.getSelectedItemPosition();
			if(pos != o.supplyer && o.items.size() > 0) {
				showDialog(ASK_CLEAR_ITEMS);
				return;
			}
			o.supplyer = spFirma.getSelectedItemPosition();
			
			doc.write();
			finish();
			
			if( isOldOrder == false )
				Warehouse.open(ReturnProperties.this, doc, false);
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
		public String firma;
		
		public DeliveryData(DeliveryImpl d) {
			date = d.getDate();
			sum = (int) d.sum();
			number = d.getData().number;
			firma = ((DeliveryEx)d.getData()).firma;
		}
		
		@Override
		public String toString() {
			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
			return number + " от " + sd.format(date) + " " + 
				Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
	}
}
