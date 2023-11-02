package com.grsoft.napoleon;

import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PriceCountEx extends PriceCount {

	@Override protected int getContentViewId() {return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx) price.getData();
		TextView tv;
		String s;
		tv = (TextView)findViewById(R.id.tvWeightItem);
		s = Util.IntToScaleStr(p.weight, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false);
		tv.setText(s);
		
		tv = (TextView)findViewById(R.id.tvMinQty);
		s = Util.IntToScaleStr(p.minQty, Consts.QTY_SCALE);
		tv.setText(s);
		
		refreshWeight();
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		PriceEx pe = (PriceEx) price.getData();
		if(pe.minQty > 0) {
			int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, pe);
			if(qty < pe.minQty) {
				Toast.makeText(this, "Количество меньше минимального", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return super.isInputValid(r);
	}

	protected void refreshWeight() {
		TextView tv;
		String s;
		Price p = price.getData();
		int qty = qtyItems;
		if(cbPackets.isChecked())
			qty = (int)((long)qty * qtyInPack / Consts.QTY_SCALE);
		
		if( qty == 0 )
			qty = Consts.QTY_SCALE;
		
		tv = (TextView)findViewById(R.id.tvWeight);
		s = Util.IntToScaleStr((int)((long)p.weight * qty / Consts.QTY_SCALE), Consts.WEIGHT_SCALE, Util.DEC_DELIM, false);
		tv.setText(s);
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		refreshWeight();
	}
}
