package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateSales extends BaseActivity {
	protected boolean editMode;
	protected CreatableDocument<? extends Sales> salesImpl;
//	protected SalesImpl salesImpl = new SalesImpl();
	protected Spinner spFirma;
	protected EditText edNumber;
	protected OrgImpl oi = new OrgImpl();
	
	public static Class<? extends Activity> activity = CreateSales.class;
	
	protected void init(Sales s, Org orgW) {
    	s.sumType = orgW.costype;
	}
	
	protected int getSalesLayoutId() { return R.layout.createsales; }
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getSalesLayoutId());
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		spFirma = (Spinner) findViewById(R.id.spFirma);
	
		salesImpl = (CreatableDocument<? extends Sales>) SalesDoc.instance().create();
		salesImpl.read(rowid);
		salesImpl.close();

		Sales sales = salesImpl.getData();

		oi.getData().id = salesImpl.getData().id;
		oi.read();
		oi.close();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oi.getData().name);
        
        if (!editMode){
        	init(sales, oi.getData());
        }
        	
        if( Features.CAN_CHANGE_COST_IN_SALES ) {
        	loadCost();
        }
        
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(salesImpl.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        edNumber = (EditText) findViewById(R.id.edNumber);
        edNumber.setText(sales.number);
        //edNumber.setInputType(InputType.TYPE_NULL);
	
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(sales.remark);
	}

	protected void loadCost() {
		View v = findViewById(R.id.trCost);
		if( v != null )
			v.setVisibility(View.VISIBLE);
		
		ConfigImpl config = new ConfigImpl();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, salesImpl.getData().sumType);

		config.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
	}
	
	public static void open(Context context, long rowid, boolean editMode){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setFirmSelection();
	}

	protected void setFirmSelection() {
		try{
			if(spFirma != null){
				BaseAdapter adapter = new FirmAdapter(this); 
				spFirma.setAdapter(adapter);
				
				for(int i=0; i< adapter.getCount(); i++){
					FirmImpl f = (FirmImpl) adapter.getItem(i);
					
					if ( f != null && 
							f.getData().id.equals(
									salesImpl.getData().supplyercode)){
						spFirma.setSelection(i);
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (spFirma != null)
			closeFirmAdapter();
	}

	protected void closeFirmAdapter() {
		FirmAdapter adapter = (FirmAdapter) spFirma.getAdapter();
		
		if (adapter != null)
			adapter.close();
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
			if( salesImpl.getData().items == null || salesImpl.getData().items.size() == 0 )
				salesImpl.delete();
		}
	}
	
	protected void okDone(Context context, boolean updateSumType) {
		Sales sales = salesImpl.getData();
		
		if (sales.created == null)
			sales.created = new Date();
		
		sales.number = edNumber.getText().toString();
		
		
		if( Features.CAN_CHANGE_COST_IN_SALES ) {
			saveCost();
		}
		
		saveFirm();
					
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		sales.remark = remark.getText().toString();
		
		postOkDone(sales);
		
		if (updateSumType && salesImpl instanceof SalesImpl)
			((SalesImpl)salesImpl).updateItemsCost(sales.sumType);
		else
			salesImpl.write();
		
		DocHelper.saveDocNumber(salesImpl.getTableName(), salesImpl.getData().number);
		if(!editMode)
			Warehouse.open(context, salesImpl, false);
		
		beforeFinish();
		finish();
	}

	protected void beforeFinish() {}

	protected void saveFirm() {
		Sales sales = salesImpl.getData();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		FirmImpl firmImpl = (FirmImpl) spFirma.getSelectedItem();
		
		if( firmImpl != null && firmImpl.getData() != null) {
			Firm f = firmImpl.getData();
			int ut = sales.useTax;
			sales.supplyercode = f.id;
			sales.useTax = f.useTax;

			if(ut != sales.useTax && salesImpl instanceof SalesImpl)
				((SalesImpl)salesImpl).refreshTax();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			if( Features.CAN_CHANGE_COST_IN_SALES ) {
				Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
				int costType = spPrices.getSelectedItemPosition();
				Sales s = salesImpl.getData();
				
				if (editMode && (s.sumType != costType && costType >= 0 && s.items != null && s.items.size() > 0))
					askToApplyNewSumType(v.getContext(), costType);
				else 
					okDone(v.getContext(), false);
			} else
				okDone(v.getContext(), false);
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

	protected void postOkDone(Sales sales) {}

	protected void saveCost() {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		int costType = spPrices.getSelectedItemPosition();
		if( costType >= 0 )
			salesImpl.getData().sumType = costType;
	}
}

class FirmAdapter extends DataBaseAdapter<Firm>{

	public FirmAdapter(Context context)
			throws IllegalAccessException, InstantiationException {
		super(context, new FirmImpl());
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return getDropDownView(arg0, arg1, arg2);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		FirmImpl firmImpl = (FirmImpl) getItem(position);
		if(firmImpl != null){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tvFirmaName.setText(firmImpl.getData().name);
		}
			
		return convertView;
	}
}