package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

public class ReturnCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	private Spinner spCause;
	
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
		spCause = (Spinner)findViewById(R.id.spCause); 
		List<KeyValue> values = new ArrayList<KeyValue>();
		values.add(new KeyValue(""));
		DialogHelper.loadSpinnerWithKey(cfg, "ПричиныВозврата", values, spCause, re == null ? "" : re.cause);
		
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner)findViewById(R.id.spCause); 
		KeyValue kv = (KeyValue) sp.getSelectedItem();
		if( kv != null )
			((ReturnItemEx)item).cause = kv.key.toString();
	}

	@Override protected KeypadHelper createKeypadHelper() { return new KeypadHelper(this, R.id.edCount, ((PriceEx)price.getData()).cantdiv > 0); }
	
	@Override
	protected boolean isInputValid(Runnable r) {
		KeyValue kv = (KeyValue) spCause.getSelectedItem();
		return kv != null && kv.key.length() > 0;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, R.string.select_cause_item, Toast.LENGTH_SHORT).show();
	}
}
