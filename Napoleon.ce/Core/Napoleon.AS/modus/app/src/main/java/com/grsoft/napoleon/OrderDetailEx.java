package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.ActionPriceImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.OrderDetail.OrderItemsAdapter;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

import android.view.View;
import android.widget.BaseAdapter;

public class OrderDetailEx extends OrderDetail {
	protected void deleteItem(OrderItem orderItem) {
		if (DocType.getCurDoc() == OrderDoc.instance() && ((OrderItemEx)orderItem).action == 1) {
			PriceImpl pi = new PriceImpl();
			pi.getData().id = orderItem.id;
			pi.read();
			pi.close();
			
			ActionPriceImpl ap = new ActionPriceImpl();
			ap.getData().priceid = orderItem.id;
			ap.getData().orgid = doc.getId();
			ap.read();
			ap.close();
			
			((OrderImplEx)doc).updateQty(pi, ap, 0, 0, false);
			((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
			updateTotalSum();
			checkFocused();
		}else {
			super.deleteItem(orderItem);
		}
			
	}
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			int getResourceID() { return R.layout.orderdetail_list_rowex; }
			
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
				super.drawInternal(view, name, color, item, pos);
				
				View v = view.findViewById(R.id.ivAction);
				
				if (v != null)
					v.setVisibility(((OrderItemEx)item).action == 1 ? View.VISIBLE : View.GONE); 
			}
		});
	}
}
