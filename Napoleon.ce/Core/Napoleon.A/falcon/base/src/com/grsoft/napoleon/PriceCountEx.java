package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keypadHelper.setTargetID(R.id.edCount);
	}
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		if( document instanceof OrderImplBase<?> ) {
			DataObject oe = (DataObject) document.getData();
			OrderImplBase<?> ord = (OrderImplBase<?>)document;
			DataObject oie = ord.findItem(price.getData().id);
			Spinner sp = (Spinner)findViewById(R.id.spDiscount);
			DocHelper.refreshDiscounts(sp, 
					DocHelper.getFieldVal(oe, "iddog").toString(), oie == null ? 
							DocHelper.getFieldVal(oe, "discid").toString(): 
								DocHelper.getFieldVal(oie, "discid").toString() );
			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					DiscountItem di = (DiscountItem) arg0.getAdapter().getItem(arg2);
					int newCost = ((CostStrategyEx)CostStrategy.defaultInstance).getItemCost(price.getData(), document, di.val);
					onChangeCost(newCost);
				}

				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
			ord.setUpdateQtyHandler(this);
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner)findViewById(R.id.spDiscount);
		DiscountItem di = (DiscountItem) sp.getSelectedItem();
		DocHelper.setFieldVal(item, "discid", di.id);
		DocHelper.setFieldVal(item, "discount",  di.val);
	}
}
