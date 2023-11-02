package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.ExtrasConst;

public class ReturnPriceCount extends PriceCount {

	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override protected boolean canChangeCost() { return false; }

	@Override protected void makeSaleHistory(Price p) { }
	
	@Override protected boolean isComplexSalesHistory() { return false; }

}
