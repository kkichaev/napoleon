package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.KeypadHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

public class ReturnCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	public static void open(Context context, long priceRoid, ReturnImplEx doc) {
		Intent i = new Intent(context, ReturnCountEx.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);				
	}
	
	@Override protected boolean getStartInPack() { return false; }
	
	@Override protected int getContentViewId() { return R.layout.returncount; }
	
	@Override protected boolean isComplexSalesHistory() { return false; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ReturnImplEx)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		ReturnItemEx re = (ReturnItemEx) ((ReturnImplEx)document).findItem(price.getData().id);
		ConfigImpl cfg = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spCause); 
		DialogHelper.loadSpinnerWithKey(cfg, "ПричиныВозврата", new ArrayList<KeyValue>(), sp, re == null ? "" : re.cause);
		
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner)findViewById(R.id.spCause); 
		KeyValue kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			((ReturnItemEx)item).cause = kv.key.toString();
	}

	@Override protected KeypadHelper createKeypadHelper() { return new KeypadHelper(this, R.id.edCount, ((PriceEx)price.getData()).cantdiv > 0); }
}
