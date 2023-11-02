package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class SalesDetailFromOrder extends SalesDetailEx {
	
	OrderEx refDoc;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, SalesDetailFromOrder.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void init() {
		refDoc = new OrderEx();
		
		SalesEx src = (SalesEx) doc.getData();
		String where = "id='" + src.id + "' and orderNumber='" + src.orderBaseNumber + "'";
		DbReader r = new DbReader();
		r.select(refDoc, refDoc.getTableName(), where);
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.dlv_order);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new DlvOrdAdapter());
	}
	
	SalesItemEx getSalesItem(String id) {
		for(OrderItem oi : doc.getData().items)
			if(oi.id.equals(id))
				return (SalesItemEx) oi;
		
		return null;
	}
	
	OrderItem getOrderItem(String id) {
		for(OrderItem oi : refDoc.items)
			if(oi.id.equals(id))
				return oi;
		
		return null;
	}
	
	class DlvOrdAdapter extends OrderItemsAdapter {
		OrderItem refItem;
		
		@Override
		int getResourceID() { return R.layout.orderdeliverydetail_list_row; }
		
		
		@Override
		protected void setItems(List<OrderItem> items) {
			List<OrderItem> ref = new ArrayList<OrderItem>(items);
			items = ref;
			super.setItems(items);
			
			for(OrderItem oi : refDoc.items) {
				SalesItemEx sie = getSalesItem(oi.id);
				if(sie == null) {
					OrderItem insItem = new OrderItem();
					insItem.id = oi.id;
					insItem.cost = oi.cost;
					insItem.qty = 0;
					insItem.flags = oi.flags;
					
					items.add(insItem);
				}
			}
		}
		
		@Override
		protected long getItemSum(OrderItem item) {
			if(!(item instanceof SalesItem))
				return (long)item.qty * item.cost / Consts.QTY_SCALE;
			return super.getItemSum(item);
		}
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			refItem = getOrderItem(item.id);
			if((refItem != null && refItem.qty != item.qty) || !(item instanceof SalesItemEx)) {
				color = Color.RED;
			} else 
				color = Color.BLACK;
			
			super.drawInternal(view, name, color, item);
			
			TextView tv;
			tv = (TextView) view.findViewById(R.id.tvDispatch);
			String text = "";
			if(refItem != null)
				text = Util.IntToScaleStr(refItem.qty, Consts.QTY_SCALE);
			tv.setText(text);
			tv.setTextColor(color);
		}
	}
}
