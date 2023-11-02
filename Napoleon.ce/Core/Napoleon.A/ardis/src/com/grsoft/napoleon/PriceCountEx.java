package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx) price.getData();

		TextView tv;
		tv = (TextView)findViewById(R.id.tvMinOrder);
		tv.setText(Util.IntToScaleStr(pe.minOrder, Consts.QTY_SCALE));
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		PriceEx pe = (PriceEx) price.getData();
		int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, pe);
		if( qty < pe.minOrder) {
			MessageBox.show(this, "Ошибка", "Введенное количество меньше минимального!");
			return false;
		}
		return super.isInputValid(r);
	}
}
