package com.grsoft.napoleon;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class CreateSalesEx extends CreateSales {
	
	private static final int OF_CASH = 0x4;
	
	public int maxDiscount = 900;

	@Override
	protected int getSalesLayoutId() {
		return R.layout.createsalesex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int params = salesImpl.getData().params; 
		if( (params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbBlack)).setChecked(true);
		
		if( (params & OF_CASH) != 0 )
			((CheckBox)findViewById(R.id.cbCash)).setChecked(true);
	
		updateDisplayDiscount();
		
		ConfigImpl ci = new ConfigImpl();
		Config cfg = ci.getData();
		cfg.key = "МаксимальнаяСкидка";
		if( ci.read() ) {
			try {
				maxDiscount = Integer.parseInt(cfg.value) * Consts.SUM_SCALE;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		TextView tv = (TextView) findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DiscountInputDlg.open(CreateSalesEx.this, new InputNumber() {
					@Override
					public int getValue() {
						return ((SalesEx)salesImpl.getData()).discount;
					}

					@Override
					public void applayInput(int value, Object... params) {
						if( -value <= maxDiscount ) {
							((SalesEx)salesImpl.getData()).discount = -value;
							updateDisplayDiscount();
						} else {
							Toast.makeText(CreateSalesEx.this, "Скидка больше максимальной", Toast.LENGTH_SHORT).show();
						}
					}
				}, Consts.SUM_SCALE, false, "Введите скидку", DiscountInputDlg.Type.OnlyDiscount);
			}
		});		
	}
	
	private void updateDisplayDiscount() {
		SalesEx se = (SalesEx)salesImpl.getData();
		((TextView) findViewById(R.id.tvDiscount)).setText("Скидка,%: " + Util.IntToScaleStr(se.discount, Consts.SUM_SCALE, Util.DEC_DELIM,false));
	}
	
	
	@Override
	protected void postOkDone(Sales sales) {
		CheckBox cash = (CheckBox)findViewById(R.id.cbBlack);
		if( cash.isChecked() ) sales.params |= ParamState.ofCash;
		else sales.params &= (~ParamState.ofCash);

		cash = (CheckBox)findViewById(R.id.cbCash);
		if( cash.isChecked() ) sales.params |= OF_CASH;
		else sales.params &= (~OF_CASH);
	}
}
