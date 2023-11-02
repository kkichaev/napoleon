package com.grsoft.napoleon.util;

import java.util.ArrayList;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;

public class DocItemsRemover {
	
	boolean doing = false;
	Context context;
	OrderDetail.OrderItemsAdapter adapter;
	ImageButton button;
	OrderImplBase<? extends Order> order;
	
	ArrayList<OrderItem> items = new ArrayList<OrderItem>();
	AlertDialog alert;
	
	public DocItemsRemover(OrderDetail.OrderItemsAdapter adapter, ImageButton button, OrderImplBase<? extends Order> order) {
		context = button.getContext();

		this.adapter = adapter;
		this.order = order;
		this.button = button;
		
		button.setImageResource(R.drawable.del_string);
		button.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { doRemove(); }
		});
	}
	
	void removeItems() {
		PriceImpl p = new PriceImpl();
		Price prc = p.getData();
		
		for(OrderItem i : items) {
			prc.id = i.id;
			p.read();
			order.updateQty(p, 0, 0, false);
		}
		p.close();
		restoreItems();
	}

	protected void restoreItems() {
		button.setImageResource(R.drawable.del_string);
		adapter.notifyDataSetChanged();
		items.clear();
	}
	
	protected void doRemove() {
		doing = !doing;
		if( doing ) {
			button.setImageResource(R.drawable.apply);
			items.clear();
		} else {
			if(items.size() > 0) {
				if(alert == null) {
					AlertDialog.Builder b = new AlertDialog.Builder(context);
					b.setTitle(R.string.confirm_title);
					b.setMessage(R.string.confirm_remove_items);
					
					b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							restoreItems();
						}
						
					});					
					b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							removeItems();
						}
					});
					alert = b.create();
				}
				alert.show();
			} else
				button.setImageResource(R.drawable.del_string);				
		}
	}

	public boolean itemClicked(OrderItem item) {
		if( !doing )
			return false;
		
		if( inSet(item) )
			items.remove(item);
		else
			items.add(item);
		adapter.notifyDataSetChanged();
		return true;
	}
	
	public boolean inSet(OrderItem item) {
		return doing && items.contains(item);
	}
}
