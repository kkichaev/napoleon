package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
	
	ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createsales);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		ConfigImpl config = new ConfigImpl();
		
		salesImpl.read(rowid);
		SalesEx sales = (SalesEx) salesImpl.getData();

		OrgImpl oi = new OrgImpl();
		oi.getData().id = salesImpl.getData().id;
		oi.read();
		oi.close();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);
        
        if (!editMode){
        	sales.sumType = oi.getData().costype;
        }
        
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "ќрганизаци€", firms, spFirma, sales.supplyer);        
        
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, sales.sumType);
		spPrices.setEnabled(false);

		Spinner spTypeDoc = (Spinner) findViewById(R.id.spDocType);
		DialogHelper.loadSpinnerWithKey(config, "“ипыƒокументов", new ArrayList<KeyValue>(), spTypeDoc, sales.docType);
		config.close();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!salesImpl.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        edNumber = (EditText) findViewById(R.id.edNumber);
        edNumber.setText(sales.number);
	}
	
	@Override
	protected void onDestroy() {
		salesImpl.close();
		super.onDestroy();
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
			
			SalesEx sales = (SalesEx) salesImpl.getData();
			
			if (sales.created == null)
				sales.created = new Date();
			
			sales.number = edNumber.getText().toString();

			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			int suppl = spFirma.getSelectedItemPosition();

			if( suppl >= 0 ) {
				sales.supplyer = suppl;
				sales.supplyercode = firms.get(suppl).toString();
			}
			
			if( costType >= 0 )
				sales.sumType = costType;
			
			Spinner spTypeDoc = (Spinner) findViewById(R.id.spDocType);
			KeyValue kv = (KeyValue) spTypeDoc.getSelectedItem();
			if( kv != null )
				sales.docType = kv.key.toString();

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			sales.remark = remark.getText().toString();
			
			salesImpl.write();
			
			if(!editMode)
				Warehouse.open(SalesProperties.this, salesImpl, false);
			
			finish();
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