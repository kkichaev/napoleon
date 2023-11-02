package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	private static final String QTY_COUNT = "qty_count";
	private TextView tvQuant;
	private int startQty = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvQuant = (TextView) findViewById(R.id.tvQuant);
		tvQuant.setText(Util.IntToScaleStr(((PriceEx)price.getData()).quant, Consts.QTY_SCALE));
		startQty = qtyItems;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		
		int qty = qtyItems;

		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		int quant = ((PriceEx)price.getData()).quant;// * Consts.QTY_SCALE;
		if(quant != 0 && (qty % quant != 0)) {
			Toast.makeText(this, "Необходимо сделать заказ кратно " + Util.IntToScaleStr(quant, Consts.QTY_SCALE), Toast.LENGTH_SHORT).show();
			edCount.setText(Util.IntToScaleStr((int) startQty, Consts.QTY_SCALE));
			edCount.selectAll();
			return false;
		}

		
		return result;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
}





