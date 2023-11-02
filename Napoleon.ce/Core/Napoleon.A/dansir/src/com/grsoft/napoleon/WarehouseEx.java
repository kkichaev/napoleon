package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	@SuppressWarnings("unchecked")
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_COST || type == COLUMN_COST_SUM) {
			Itemsable id = (Itemsable)document;
			long value = 0;

			if( type == COLUMN_COST_SUM )
				value = id.getItemSum(price);
			if( value == 0 ) {
				value =  CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price, document);				
				value = (((long)value * price.qtyInPack) / Consts.QTY_SCALE);
			}
			
			textView.setText(Util.IntToScaleStr(value, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		} else
			super.setTextColumnValue(textView, type, price);
	}
}
