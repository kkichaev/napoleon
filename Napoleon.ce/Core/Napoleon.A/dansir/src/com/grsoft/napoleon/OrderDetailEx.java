package com.grsoft.napoleon;

import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {

	static public final int MIN_ORDER_SUM = 70000;
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		String qtyText;
		PriceEx p = (PriceEx) price.getData();
		int inPack = p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
		qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + p.packName;

		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	@Override
	public void send() {
		if(doc.sum() < MIN_ORDER_SUM) {
			makeSumWarning();
			return;
		}
		super.send();
	}
	
	void makeSumWarning() {
		Toast.makeText(this, "Сумма заяки меньше минимальной (" + 
				Util.IntToScaleStr(MIN_ORDER_SUM, Consts.SUM_SCALE, Util.DEC_DELIM, false) + ")", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(doc.getData().items != null && doc.getData().items.size() > 0 && doc.sum() < MIN_ORDER_SUM) {
				makeSumWarning();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
