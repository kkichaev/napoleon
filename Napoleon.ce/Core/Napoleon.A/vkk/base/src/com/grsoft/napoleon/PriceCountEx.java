package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		edCount.requestFocus();
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		PriceEx pe = (PriceEx)price.getData();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvMult);
		tv.setText(Util.IntToScaleStr(pe.mult, Consts.QTY_SCALE));
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		PriceEx pe = (PriceEx)price.getData();
		int qty = qtyItems;
		if( cbPackets.isChecked() )
			qty = (int)(((long)qty * qtyInPack) / Consts.QTY_SCALE);
		
		if( pe.mult > Consts.QTY_SCALE && (qty %pe.mult) != 0 ) {
			String error = getResources().getString(R.string.mult_error);
			String errMsg = String.format(error, pe.mult/Consts.QTY_SCALE);
			Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
			
			return false;
		}
		return true;
	}
}
