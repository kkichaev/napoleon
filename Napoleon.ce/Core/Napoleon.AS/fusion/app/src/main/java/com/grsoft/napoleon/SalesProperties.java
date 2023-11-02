package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class SalesProperties extends BaseActivity {
	private boolean editMode;
	private SalesImpl salesImpl = new SalesImpl();
	private EditText edNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createsales);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		ConfigImpl config = new ConfigImpl();
		
		salesImpl.read(rowid);
		salesImpl.close();
		final SalesEx sales = (SalesEx) salesImpl.getData();

		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = sales.id;
		oi.read();
		oi.close();
        ((TextView) findViewById(R.id.tvOrgName)).setText(org.name);
        
        if (!editMode) {
        	sales.sumType = oi.getData().costype;
        }


		Spinner s = (Spinner)findViewById(R.id.spFirma);
        DialogHelper.loadSpinnerFromDataObject(s, Firm.class, new DialogHelper.Selected<Firm>() {
			@Override public boolean isSelected(Firm object) { return sales.supplyercode.equals(object.id); }
		}, false, "name");
//        Firm f = new Firm();
//        ArrayList<KeyValue> values = new ArrayList<KeyValue>();
//        DbReader r = new DbReader();
//        int selected = -1;
//        boolean bdo = r.select(f, DataObjectInfo.getInstance().getTableName(f.getClass()), "", "name");
//        while( bdo ) {
//        	KeyValue kv = new KeyValue(f.id, f.name);
//        	if( kv.key.equals(sales.supplyercode) )
//        		selected = values.size();
//        	values.add(kv);
//        	f = new Firm();
//        	bdo = r.selectNext(f);
//        }
//		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
//		s.setAdapter(aa);
//		if( selected >= 0 && selected < s.getCount())
//			s.setSelection(selected);

		int selected;
		selected = -1;
		List<KeyValue> values = new ArrayList<KeyValue>();
		for(OrgDogovor od : org.dogovors) {
			KeyValue kv = new KeyValue(od.id, od.name);
        	if( kv.key.equals(sales.dogovor) )
        		selected = values.size();
        	values.add(kv);
		}
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		s = (Spinner)findViewById(R.id.spDog);
		s.setAdapter(aa);
		if( selected >= 0 && selected < s.getCount())
			s.setSelection(selected);
        	
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, sales.sumType);
		config.close();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!salesImpl.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        edNumber = (EditText) findViewById(R.id.edNumber);
        edNumber.setText(sales.number);
	}
	
	public static void open(Context context, long rowid, boolean editMode){
		Intent intent = new Intent(context, SalesProperties.class);
		intent.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyDoc();			
			finish();
		}
	}
	
	private void deleteEmptyDoc() {
		if(!editMode) {
			if( salesImpl.getData().items == null || 
					salesImpl.getData().items.size() == 0 )
				salesImpl.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();
			
			if (editMode && (salesImpl.getSumType() != costType && costType >= 0))
				askToApplyNewSumType(v.getContext(), costType);
			else 
				okDone(v.getContext(), false);
		}
		
		private void okDone(Context context, boolean updateSumType) {
			SalesEx sales = (SalesEx) salesImpl.getData();
			
			if (sales.created == null)
				sales.created = new Date();
			
			sales.number = edNumber.getText().toString();
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			Firm selF = (Firm) spFirma.getSelectedItem();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( selF != null) {
				sales.supplyercode = selF.id;
				sales.useTax = selF.useTax;
			}

			KeyValue val = (KeyValue) ((Spinner)findViewById(R.id.spDog)).getSelectedItem();
			if( val != null)
				sales.dogovor = val.key.toString();

			if( costType >= 0 )
				sales.sumType = costType;
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			sales.remark = remark.getText().toString();
			
			if (updateSumType)
				salesImpl.updateItemsCost(sales.sumType);
			else
				salesImpl.write();
			
			if(!editMode)
				Warehouse.open(context, salesImpl, false);
			
			finish();
		}
		
		private void askToApplyNewSumType(final Context context, final int newSumType){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("¬нимание");
			builder.setMessage("“ип цены был изменен, пересчитать заказ?");

			builder.setPositiveButton("ѕересчитать", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(context, true);
				}
			});
			
			builder.setNegativeButton("ќставить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(context, false);
				}
			});
			
			builder.create().show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyDoc();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}