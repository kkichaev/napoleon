package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl3_62;
import com.grsoft.dataobjects.impl.PriceImpl;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class OrderDetail3_62 extends OrderDetail{ 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(Menu.NONE, R.id.avg_order, Menu.NONE, R.string.avg_order);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.avg_order) {
			avgOrder();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	private void avgOrder() {
		clearDoc();
		//((OrderImpl3_62)doc).avgOrder();
		
		Toast.makeText(this, "Ќет решений как считать средний заказ.....", Toast.LENGTH_LONG).show();
		postUpdateDoc();
	}

	private void clearDoc() {
		List<OrderItem> items = new ArrayList<OrderItem>(doc.getData().items);
		
		for(OrderItem i : items) 
			deleteSilentItem(i);
	}
	
	protected void postUpdateDoc() {
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
		updateTotalSum();
		checkFocused();
	}
	
	protected void deleteSilentItem(OrderItem orderItem) {
		PriceImpl pi = new PriceImpl();
		pi.getData().id = orderItem.id;
		pi.read();
		pi.close();
		doc.updateQty(pi, 0, 0, false);
	}
}
