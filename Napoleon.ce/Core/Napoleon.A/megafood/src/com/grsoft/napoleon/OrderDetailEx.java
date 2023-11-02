package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.FocusRejectReason;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderRejectItem;
import com.grsoft.dataobjects.impl.FocusRejectReasonImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail {
	
	FocusRejectReasonImpl fi = new FocusRejectReasonImpl();
	
	@Override
	protected void onDestroy() {
		fi.close();
		super.onDestroy();
	}
	
	@Override
	public void send() {
		OrderImplEx oe = (OrderImplEx)doc; 
		if(!oe.isValid()) {
			Toast.makeText(this, "Не весь фокусный ассортимент заполнен.", Toast.LENGTH_SHORT).show();
			return;
		}
		super.send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.no_valid_order) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("Не весь фокусный ассортимент заполнен. Заявка не будет отправлена. Продолжить?");
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
			});
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { 
					dialog.dismiss(); 
					finish();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	@Override
	public void onBackPressed() {
		OrderImplEx oe = (OrderImplEx)doc; 
		if(!oe.isValid() && !oe.isEmpty()) {
			showDialog(R.id.no_valid_order);
			return;
		}
		super.onBackPressed();
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected void setItems(List<OrderItem> src) {
			this.items = new ArrayList<OrderItem>();
			for(OrderItem oi : src)
				items.add(new OrderItemDetail(oi));

			for(OrderRejectItem ori : ((OrderEx)doc.getData()).rejectItems)
				items.add(new OrderItemDetail(ori));
		}
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
			
			TextView tv = (TextView)view.findViewById(R.id.tvReason);
			String reason = ((OrderItemDetail)item).reason;
			if(reason != null) {
				FocusRejectReason f = fi.getData();
				f.id = reason;
				fi.read();
				tv.setText(f.name);
				tv.setVisibility(View.VISIBLE);
				view.setBackgroundResource(R.drawable.lt_grey_selector);
			} else {
				tv.setVisibility(View.GONE);
				view.setBackgroundResource(R.drawable.list_selector);
			}
		}
		
		@Override
		int getResourceID() {
			return R.layout.orderdetail_list_rowex;
		}
	}
}

class OrderItemDetail extends OrderItem {
	public String reason = null;
	
	public OrderItemDetail(OrderItem oi) {
		id = oi.id;
		qty = oi.qty;
		cost = oi.cost;		
	}
	
	public OrderItemDetail(OrderRejectItem ri) {
		id = ri.id;
		cost = 0;
		qty = 0;
		reason = ri.reason;
	}
}
