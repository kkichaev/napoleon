package com.grsoft.napoleon;

import java.util.Date;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

public class CreateSalesEx extends BaseActivity {
	private boolean editMode;
	protected CreatableDocument<? extends Sales> salesImpl = new SalesImplEx();
	protected Spinner spFirma;
	private EditText edNumber;

	OrgImpl oi = new OrgImpl();
	OrgEx org;

	protected int getSalesLayoutId() { return R.layout.createsalesex; }
	
	protected void init(Sales s, Org o) {
		OrgEx oe = (OrgEx)o;
		SalesEx sls = (SalesEx)s;
		sls.costCode = oe.costCode;
		sls.ido = oe.ido;
    	sls.sumType = oe.costype;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getSalesLayoutId());
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		salesImpl.read(rowid);
		salesImpl.close();
		SalesEx sales = (SalesEx)salesImpl.getData();

		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = sales.id;
		oi.read();
		oi.close();
        ((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);
        
        if (!editMode)
        	init(sales, oe);
        	        
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(salesImpl.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        edNumber = (EditText) findViewById(R.id.edNumber);
        edNumber.setText(salesImpl.getData().number);
        edNumber.setEnabled(false);
	
    	loadCost();
		OrgDogovorImpl.loadFirms((Spinner)findViewById(R.id.spFirma), 
				(Spinner)findViewById(R.id.spDog),
				sales.ido, sales.supplyercode, sales.dogovor);		
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(sales.remark);
	}
	
	protected void saveCost() {
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		CostData cd = (CostData) spPrices.getSelectedItem();
		
		if (cd != null) {
		SalesEx sales = (SalesEx)salesImpl.getData();
			sales.costCode = cd.id;
			sales.sumType = cd.index;
		}
	}
	
	protected void loadCost() {
		SalesEx sales = (SalesEx)salesImpl.getData();
		
		org = (OrgEx) oi.getData();		
		org.id = sales.id;
		oi.read();
		oi.close();
		
		CostList values = new CostList();
		values.loadCost(org);		

		Spinner spCost = (Spinner)findViewById(R.id.spPrices);
		ArrayAdapter<CostData> aa = new ArrayAdapter<CostData>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spCost.setAdapter(aa);
		
		spCost.setEnabled(false);		
		
		int index = 0;
		for(CostData cd : values) {
			if( cd.id.equals(sales.costCode) ) {
				spCost.setSelection(index);
				break;
			}
			index++;
		}
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
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			if( Features.CAN_CHANGE_COST_IN_SALES ) {
				Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
				int costType = spPrices.getSelectedItemPosition();
				Sales s = salesImpl.getData();
				
				if (editMode && (s.sumType != costType && costType >= 0 && s.items != null && s.items.size() > 0)) {
					okDone(v.getContext(), true);
					return;
				}
			}
			okDone(v.getContext(), false);
		}
		
		private void okDone(Context context, boolean updateSumType) {
			SalesEx sales = (SalesEx) salesImpl.getData();
			
			if (sales.created == null)
				sales.created = new Date();
			
			sales.number = edNumber.getText().toString();			
			saveCost();
			
			FirmEx firm = (FirmEx)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
			if( firm != null )
				sales.supplyercode = firm.id;
						
			OrgDogovor dog = (OrgDogovor)((Spinner)findViewById(R.id.spDog)).getSelectedItem();
			if( dog != null ) {
				sales.dogovor = dog.idDog;
				sales.delay = dog.delay;
			}

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			sales.remark = remark.getText().toString();
			
			if (updateSumType)
				((SalesImpl)salesImpl).updateItemsCost(sales.sumType);
			else
				salesImpl.write();
			
			if(!editMode) {
				DocHelper.saveDocNumber(salesImpl.getTableName(), salesImpl.getData().number);
				Warehouse.open(context, salesImpl, false);
			}
			
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
