package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

public class ReturnPriceCount extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}

	@Override protected int getContentViewId() { return R.layout.return_price_count; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((ReturnImplEx)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		ReturnItemEx re = (ReturnItemEx) ((ReturnImplEx)document).findItem(price.getData().id);
		ConfigImpl ci = new ConfigImpl();
		Spinner sp = (Spinner) findViewById(R.id.spCause);
		DialogHelper.loadSpinnerFromConfig(ci, "ПричиныВозврата", new ArrayList<CharSequence>(), sp, re != null ? re.cause : "");
		ci.close();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner) findViewById(R.id.spCause);
		CharSequence sel = (CharSequence) sp.getSelectedItem();
		if(sel != null) {
			((ReturnItemEx)item).cause = sel.toString();
		}
	}
}
