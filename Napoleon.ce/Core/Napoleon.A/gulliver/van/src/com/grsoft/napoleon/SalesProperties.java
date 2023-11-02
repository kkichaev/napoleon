package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrderExtended;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgExtended;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;
import android.app.AlertDialog;
import android.app.Dialog;
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

public class SalesProperties extends BaseActivity {
	private boolean editMode;
	protected OrderImplBase<? extends Sales> salesImpl = (SalesImpl) SalesDoc.instance().create();
	private OrgImpl orgImpl;
	private Spinner spDog;
	private Map<String, OrgDogovor> dgvCache = new HashMap<String, OrgDogovor>();
	private DateHandler dateHandler;
	private static final int DIALOG_DATE_PICKER_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createsales);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		ConfigImpl config = new ConfigImpl();
		
		salesImpl.read(rowid);
		salesImpl.close();
		Sales sales = salesImpl.getData();

		orgImpl = new OrgImpl();
		orgImpl.getData().id = salesImpl.getData().id;
		orgImpl.read();
        ((TextView) findViewById(R.id.tvOrgName)).setText(orgImpl.getData().name);
        
        if (!editMode){
        	sales.sumType = orgImpl.getData().costype;
        }
        
        Firm f = new Firm();
        ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
        DbReader r = new DbReader();
        int selected = -1;
        boolean bdo = r.select(f, DataObjectInfo.getInstance().getTableName(f.getClass()), "", "name");
        while( bdo ) {
        	KeyValue kv = new KeyValue(f.id, f.name);
        	if( kv.key.equals(sales.supplyercode) )
        		selected = firms.size();
        	firms.add(kv);
        	f = new Firm();
        	bdo = r.selectNext(f);
        }
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, firms);
		Spinner s = (Spinner)findViewById(R.id.spFirma);
		s.setAdapter(aa);
		if( selected >= 0 && selected < s.getCount())
			s.setSelection(selected);
        
        	
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, sales.sumType);
		spPrices.setEnabled(false);
		config.close();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(salesImpl.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        
        spDog = CreateOrder.createDogovorSpinner(this, (OrderExtended)salesImpl.getData(), (OrgExtended)orgImpl.getData(), dgvCache);
        
        EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(sales.remark);
        
//        if (!salesImpl.isExported()){
//	        spDog.setOnItemSelectedListener(new OnItemSelectedListener() {
//	
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					KeyValue kv = (KeyValue) arg0.getAdapter().getItem(arg2);
//					List<OrgDogovor> dogovors = ((OrgExtended)orgImpl.getData()).getDogovors();
//					
//					if (dogovors != null){
//						for(OrgDogovor od : dogovors){
//							if (kv.key.equals(od.id)){
//								//((SalesImplEx)salesImpl).convertDogNumber(od.isGeneral());
//								break;
//							}
//						}
//					}
//					
//					edNumber.setText(salesImpl.getData().number);
//				}
//	
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//        }
		
		TextView tv = (TextView)findViewById(R.id.tvDate);
		dateHandler = new DateHandler(tv, sales.date, DIALOG_DATE_PICKER_ID);
		tv.setEnabled(!editMode);
		
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
			boolean isGeneral = false;
			isGeneral = getSelectedDocType();
			SalesEx salesEx = (SalesEx) salesImpl.getData();
			
			if (editMode && isGeneral != salesEx.isGenDoc()) 
				askToApplyNewDocType(v.getContext(), isGeneral);
			else 
				okDone(v.getContext(), false, isGeneral);
		}
		
		private void okDone(Context context, boolean createDocNumber, boolean isGeneral) {
			Sales sales = salesImpl.getData();
			
			if (sales.created == null)
				sales.created = new Date();
			
			if (dateHandler != null)
				sales.date = Util.resetTime(dateHandler.getDate());
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			KeyValue f = (KeyValue) spFirma.getSelectedItem();
			Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
			int costType = spPrices.getSelectedItemPosition();

			if( f != null)
				sales.supplyercode = f.key.toString();
			
			if( costType >= 0 )
				sales.sumType = costType;
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			sales.remark = remark.getText().toString();
			
			KeyValue od = (KeyValue) spDog.getSelectedItem();
			SalesEx salesEx = (SalesEx)salesImpl.getData(); 
			salesEx.dogCode = (String) od.key;
			
			if (CreateOrder.handleBlockedDogovor(SalesProperties.this, dgvCache, salesEx.dogCode))
				return;
			
			if(sales.number.trim().length() == 0 || createDocNumber){
				isGeneral = getSelectedDocType();
				((SalesImplEx)salesImpl).convertNumberType(isGeneral);
			}
			
			salesImpl.write();
			
			if(!editMode)
				Warehouse.open(context, salesImpl, false);
			
			finish();
		}

		protected boolean getSelectedDocType() {
			boolean result = false;
			
			KeyValue od = (KeyValue) spDog.getSelectedItem();
			List<OrgDogovor> dogovors = ((OrgExtended)orgImpl.getData()).getDogovors();
			
			if (dogovors != null){
				for(OrgDogovor dgv : dogovors){
					if (od.key.equals(dgv.id)){
							result = dgv.isGeneral();
						break;
					}
				}
			}
			return result;
		}
		
		public void askToApplyNewDocType(final Context context, final boolean isGeneral) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("¬нимание");
			builder.setMessage("“ип договора был изменен, номер будет изменен, изменить?");

			builder.setPositiveButton("»зменить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(context, true, isGeneral);
				}
			});
			
			builder.setNegativeButton("ќставить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(context, false, isGeneral);
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
		}
		
		return super.onCreateDialog(id);
	}
}