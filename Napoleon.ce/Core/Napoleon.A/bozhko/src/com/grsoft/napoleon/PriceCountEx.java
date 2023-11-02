package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.AgentSalesPlanImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	private final static int NOT_PLAN = -1;
	private int planQty = NOT_PLAN;
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected void refreshData() {
		super.refreshData();
		View v = findViewById(R.id.trPlan);
		SalesDataItem planItem = AgentSalesPlanImpl.getItemQty(price.getData().id);
		
		if( planItem == null ) 
			v.setVisibility(View.GONE);
		else {
			v.setVisibility(View.VISIBLE);
			TextView tv;
			tv = (TextView)findViewById(R.id.tvPlanText);
			tv.setText("План до " + Util.simpleDateFormat.format(planItem.date));
			planQty = planItem.qty;
			planQty += qtyItems;
			tv = (TextView)findViewById(R.id.tvPlanQty);
			tv.setText(Util.IntToScaleStr(planItem.qty, Consts.QTY_SCALE));
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DocType.getCurDoc() == OrderDoc.instance())
			btnOK.setOnClickListener(new BtnOKClickListenet(){
				@Override
				public void onClick(View v) {
					if(planQty != NOT_PLAN && ((planQty - qtyItems) <= 0) && ((OrderImpl)document).getItemQty(price.getData())  != qtyItems )
						showDialog(R.id.plan_exceed_dlg);
					else
						super.onClick(v);
				}
			});
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.plan_exceed_dlg:
			return createPlanExceedDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createPlanExceedDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.plan_exceed, null);
		result.setView(view);
		
		view.findViewById(R.id.btnSale).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new BtnOKClickListenet().onClick(v);
			}
		});
		
		view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(R.id.plan_exceed_dlg);
			}
		});
		return result.create();
	}
}
