package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;

public class ReturnPriceCount extends PriceCount {
	DeliveryImpl delivery = new DeliveryImpl();
	Delivery dlv;

	public static void open(Context context, long priceRoid,
			DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);

		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}

	@Override
	protected void refreshData() {
		dlv = delivery.getData();
		
		if(document instanceof ReturnImplEx){
			ReturnEx rt = (ReturnEx) document.getData();
			dlv.id = rt.id;
			dlv.number = rt.dlvNum;
			
			delivery.read();
			delivery.close();
		}
		
		super.refreshData();
	}

	@Override
	protected void makeSaleHistory(Price p) {
	}

	@Override
	protected boolean isComplexSalesHistory() {
		return false;
	}

	@Override
	protected boolean isInputValid(Runnable r) {

		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		if (dlv.items != null)
			for (DeliveryItem item : dlv.items) {
				if (item.id.equals(price.getData().id)) {
					if (qty > item.qty) {
						Toast.makeText(
								this,
								"Введенное количество больше количества в накладной",
								Toast.LENGTH_SHORT).show();
						return false;
					}
				}
			}

		return true;
	}

	@Override
	protected int getInputCost(Price p) {
		if (document instanceof ReturnImplEx) {
			int result = 0;

			if (delivery.getRowid() != ExtrasConst.INVALID_ID) {
				for (DeliveryItem item : dlv.items) {
					if (item.id.equals(p.id)) {
						result = ((DeliveryItemEx) item).cost;
						break;
					}
				}
			}

			return result;
		} else
			return super.getInputCost(p);
	}

}
