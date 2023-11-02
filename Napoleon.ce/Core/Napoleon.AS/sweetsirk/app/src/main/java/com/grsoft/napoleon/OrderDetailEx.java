package com.grsoft.napoleon;

import android.graphics.Color;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void updateTotalSum() {
		((OrderDocEx)OrderDocEx.instance()).updateTotalSum(this, doc.sum(), doc.weight(), 
				((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count(), 
				R.id.tvTotalSum,
				((OrderImplEx)doc).sumDisc());
		
		
	}

	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			protected int getItemColor(int pos) {
				OrderItem item = (OrderItem) getItem(pos);
				PriceEx p = (PriceEx) price.getData();
				p.id = item.id;
				price.read();
				price.close();

				if (p.expdate == 1)
					return Color.GREEN;

				return super.getItemColor(pos);
			}
		});
	}
}
