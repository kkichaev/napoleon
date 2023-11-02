package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;

public class PriceCountBonus extends PriceCount{
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, PriceCountBonus.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		((CfgNpl)ConfigManager.getConfig()).checkPrice = false;
	}
}
