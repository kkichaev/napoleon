package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CreateSalesEx extends CreateSales {
	private static final String SIMPLE_MODE = "simple_mode"; 
	private boolean simpleMode = false;
	
	public static void open(Context context, long rowid, boolean editMode, boolean simpleMode){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(SIMPLE_MODE, simpleMode);
		context.startActivity(intent);
	}
	
	@Override
	protected int getSalesLayoutId() {
		return R.layout.createsalesex;
	}
	
	boolean doChecking = false;
	List<CheckBox> checkBoxes = new ArrayList<CheckBox>(); 
	
	CompoundButton.OnCheckedChangeListener checkButtons = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if(doChecking)
				return;
			
			doChecking = true;
			if(arg1) {
				for(CheckBox cb : checkBoxes) {
					if(cb != arg0 && cb.isChecked())
						cb.setChecked(false);
				}
			}
			doChecking = false;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.trFirm).setVisibility(View.GONE);
		findViewById(R.id.edNumber).setEnabled(false);
		
//		CheckBox isBlack = (CheckBox)findViewById(R.id.cbIsBlack); 
//		isBlack.setChecked(((SalesEx)salesImpl.getData()).isBlack > 0);
//		doChecking = true;
//		checkBoxes.add(isBlack);
//		isBlack.setOnCheckedChangeListener(checkButtons);
		
		simpleMode = getIntent().getBooleanExtra(SIMPLE_MODE, false);
		
		SalesEx doc = (SalesEx) salesImpl.getData();
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = doc.id;
		oi.read();
		oi.close();
		
		if(oe.isAZS > 0) {
			findViewById(R.id.trAZS).setVisibility(View.VISIBLE);
			CheckBox exch = (CheckBox)findViewById(R.id.cbExchange);
			CheckBox expi = (CheckBox)findViewById(R.id.cbExpired); 
			
			exch.setChecked(doc.isExchange > 0);
			expi.setChecked(doc.isExpired > 0);
			
			checkBoxes.add(exch);
			checkBoxes.add(expi);
			exch.setOnCheckedChangeListener(checkButtons);
			expi.setOnCheckedChangeListener(checkButtons);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doChecking = false;
	}
	
	@Override
	protected void init(Sales s, Org orgW) {
		super.init(s, orgW);
		((SalesEx)s).taxType = ((OrgEx)orgW).taxType;
	}

	@Override
	protected void saveFirm() {
		super.saveFirm();
		SalesEx se = (SalesEx)salesImpl.getData();
		se.useTax = se.taxType != OrgEx.TAX_NONE ? 1 : 0;
	}

	@Override
	protected void postOkDone(Sales sales) {
		SalesEx se = (SalesEx)sales;
//		se.isBlack = ((CheckBox)findViewById(R.id.cbIsBlack)).isChecked() ? 1 : 0;
		se.isExpired = ((se.isBlack == 0) && ((CheckBox)findViewById(R.id.cbExpired)).isChecked()) ? 1 : 0;
		se.isExchange = ((se.isBlack == 0) && (se.isExpired == 0) && ((CheckBox)findViewById(R.id.cbExchange)).isChecked()) ? 1 : 0;
	}
	
	@Override
	protected void beforeFinish() {
		super.beforeFinish();
		if(simpleMode)
			SalesDetailSM.open(this, salesImpl.getRowid());
	}
}
