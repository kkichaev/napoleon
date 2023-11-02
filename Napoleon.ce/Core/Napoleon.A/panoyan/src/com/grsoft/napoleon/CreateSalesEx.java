package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.modules.print.util.DocHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class CreateSalesEx extends CreateSales {
	
	@Override
	protected int getSalesLayoutId() {
		return R.layout.createsalesex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//findViewById(R.id.trFirm).setVisibility(View.GONE);
		findViewById(R.id.edNumber).setEnabled(false);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbIsBlack);
		cb.setChecked(((SalesEx)salesImpl.getData()).isBlack > 0);
		
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(salesImpl.isEditable()) {
					SalesEx se =  (SalesEx) salesImpl.getData();
					se.isBlack = isChecked ? 1 : 0;
					se.number = DocHelper.makeDocNumber(salesImpl);
					edNumber.setText(se.number);
				}
			}
		});
	}
	
	@Override
	protected void postOkDone(Sales sales) {
		SalesEx se = (SalesEx)sales;
		se.isBlack = ((CheckBox)findViewById(R.id.cbIsBlack)).isChecked() ? 1 : 0;

		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		FirmImpl firmImpl = (FirmImpl) spFirma.getSelectedItem();
		if(firmImpl != null) {
			int prev = se.useTax;
			se.useTax = ((FirmEx)firmImpl.getData()).useTax;
			if(se.useTax != prev) {
				((SalesImplEx)salesImpl).refreshTax();
			}
		}
	}
}
