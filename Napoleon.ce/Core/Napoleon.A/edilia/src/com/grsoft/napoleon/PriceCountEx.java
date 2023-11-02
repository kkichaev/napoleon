package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecount_new_ex; }

	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed == 0;
	}
	
	@Override
	protected int getStartValue() {
		return Consts.QTY_SCALE;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();
		
		
		
		String unit = getString(pe.boxed == 0 ? R.string.box_lbl : R.string.qty_lbl);
		
		View v = findViewById(R.id.trPacket);
		v.setVisibility(View.GONE);//(pe.boxed > 0 || pe.qtyInPack == Consts.QTY_SCALE ) ? View.GONE : View.VISIBLE); 
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvQtyOrder);
		String str = getString(R.string.orderQty) + "\n" + unit;
		tv.setText(str);

		tv = (TextView)findViewById(R.id.tvOnWh);
		str = getString(R.string.onWh) + ", " + unit;
		tv.setText(str);
		
		if(pe.boxed == 0) {
			int whQty = (int)((long)pe.qty * Consts.QTY_SCALE / qtyInPack);
			((TextView) findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
		}
		
//		if(pe.boxed == 0) {
//			// Весовой товар, в форме подбора кол-ва цена должна быть за ящ
//			int boxCost = (int)(((long)priceVal * pe.qtyInPack  + Consts.QTY_SCALE/ 2)/ Consts.QTY_SCALE);
//			((TextView)findViewById(R.id.tvPrice)).setText(Util.IntToScaleStr(boxCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
//		}
		
//		if( pe.boxed > 0)
//		{
//			TextView tvQty = (TextView) findViewById(R.id.tvQty);
//			int whQty = (int)((long)pe.qty * Consts.QTY_SCALE / pe.qtyInPack);
//			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
//		}
	}
}
