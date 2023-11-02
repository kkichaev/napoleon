package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView)findViewById(R.id.tvMinQty);
		tv.setText(Util.IntToScaleStr(((PriceEx)price.getData()).limit, Consts.QTY_SCALE));
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		if( qty < ((PriceEx)price.getData()).limit ) {
			Toast.makeText(this, R.string.qty_below_limit, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tv = (TextView)findViewById(R.id.tvRezQty);
		PriceEx pe = (PriceEx)price.getData();
		tv.setText(Util.IntToScaleStr(pe.rezQty, Consts.QTY_SCALE));
	}
}
