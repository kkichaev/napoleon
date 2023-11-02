package com.grsoft.napoleon;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.trFirm).setVisibility(View.GONE);
		findViewById(R.id.edNumber).setEnabled(false);
		
		((CheckBox)findViewById(R.id.cbIsBlack)).setChecked(((SalesEx)salesImpl.getData()).isBlack > 0);
		
		simpleMode = getIntent().getBooleanExtra(SIMPLE_MODE, false);
	}
	
	@Override
	protected void postOkDone(Sales sales) {
		((SalesEx)sales).isBlack = ((CheckBox)findViewById(R.id.cbIsBlack)).isChecked() ? 1 : 0;
	}
	
	@Override
	protected void beforeFinish() {
		super.beforeFinish();
		if(simpleMode)
			SalesDetailSM.open(this, salesImpl.getRowid());
	}
}
