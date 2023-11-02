package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.ConfigAgama;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PriceCountEx extends PriceCount {
	
	static final String WH_INDEX = "WhIndexTag";

	int whIndex = 0;
	boolean inited = false;
	
	public static void open(Context context, long priceRoid, OrderImplEx order) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		i.putExtra(WH_INDEX, ((OrderEx)order.getData()).whIndex);

		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();		
		whIndex = b.getInt(WH_INDEX, ((ConfigAgama)ConfigManager.getConfig()).whDefault);
		inited = false;
	
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void refreshData() {		
		if( !inited && document instanceof OrderImplEx )
			((OrderImplEx)document).setWh(whIndex);
		
		inited = true;
		super.refreshData();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if( document instanceof OrderImplEx )
			outState.putInt(WH_INDEX, whIndex);
	}
}
