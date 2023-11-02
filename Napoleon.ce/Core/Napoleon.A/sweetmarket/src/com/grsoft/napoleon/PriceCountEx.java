package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PriceCountEx extends PriceCount {
	private final static String PREV_QTY_VALUE = "prev_qty_value";
	private final static String PREV_INPACK_VALUE = "prev_inpack_value";
	
	@Override
	protected DataObject getDocItem(Price p) {
		DataObject result = super.getDocItem(p);
		
		if(result == null){
			result = new OrderItem();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			((OrderItem)result).qty = pref.getInt(PREV_QTY_VALUE, 1 * Consts.QTY_SCALE);
			((OrderItem)result).flags = pref.getInt(PREV_INPACK_VALUE, 0);
			((OrderItem)result).id = p.id;
		}
		return result;
	}
	
	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor ed = pref.edit();
		ed.putInt(PREV_QTY_VALUE, qty);
		ed.putInt(PREV_INPACK_VALUE, inPack ? 1 : 0);
		ed.commit();
		return super.updateQty(inPack, qty);
	}
}
