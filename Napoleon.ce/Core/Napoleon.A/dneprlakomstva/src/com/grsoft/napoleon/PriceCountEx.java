package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecount_new_ex; }

	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed > 0;
	}
	
	@Override
	protected int getStartValue() {
		return Consts.QTY_SCALE;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();
		String unit = getString(pe.boxed > 0 ? R.string.box_lbl : R.string.qty_lbl);
		
		View v = findViewById(R.id.trPacket);
		v.setVisibility((pe.boxed > 0 || pe.qtyInPack == Consts.QTY_SCALE ) ? View.GONE : View.VISIBLE); 
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvQtyOrder);
		String str = getString(R.string.orderQty) + "\n" + unit;
		tv.setText(str);

		tv = (TextView)findViewById(R.id.tvOnWh);
		str = getString(R.string.onWh) + ", " + unit;
		tv.setText(str);
		
		if( pe.boxed > 0)
		{
			TextView tvQty = (TextView) findViewById(R.id.tvQty);
			int whQty = (int)((long)pe.qty * Consts.QTY_SCALE / pe.qtyInPack);
			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
		}
	}
}
