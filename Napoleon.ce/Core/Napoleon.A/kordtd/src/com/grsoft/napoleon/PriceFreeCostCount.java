package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.widget.Toast;

public class PriceFreeCostCount extends PriceCount implements OrderImpl.UpdateQtyHandler {
	int minQty;
	int costWOD = 0;

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx)item;
		oie.costWOD = costWOD;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		if (document instanceof OrderImplEx) {
			((OrderImplEx)document).setUpdateQtyHandler(this);
			costWOD = ((CostStrategyEx)CostStrategy.defaultInstance).getNativeCost(price.getData(), document);
		}
	}

	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		
		minQty = ((PriceEx)price.getData()).minQty;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, getString(R.string.order_min_qty, Util.IntToScaleStr(minQty, Consts.QTY_SCALE)), Toast.LENGTH_SHORT).show();
		edCount.setText(Util.IntToScaleStr((int) minQty, Consts.QTY_SCALE));
		edCount.selectAll();
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		
		if(minQty > 0)
			result = minQty <= qty;
			
		return result;
	}
}
