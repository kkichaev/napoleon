package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;

import android.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(Menu.NONE, R.id.clear_order, Menu.NONE, R.string.clear_order);
		menu.add(Menu.NONE, R.id.avg_order, Menu.NONE, R.string.avg_order);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.clear_order) {
			clearOrder();
			return true;
		}else if (item.getItemId() == R.id.avg_order) {
			avgOrder();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	private void avgOrder() {
		clearDoc();
		((OrderImplEx)doc).avgOrder();
		postUpdateDoc();
	}

	private void clearDoc() {
		List<OrderItem> items = new ArrayList<OrderItem>(doc.getData().items);
		
		for(OrderItem i : items) 
			deleteSilentItem(i);
	}
	
	private void clearOrder() {
		clearDoc();
		postUpdateDoc();
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
	
	PriceImpl pi = new PriceImpl();
	
	int getPriceOrder(String rid) {
		pi.read("id", rid);
		return ((PriceEx)pi.getData()).type;
	}
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			PriceCmp cmp = new PriceCmp();
			
			@Override
			protected void setItems(List<OrderItem> items) {
				Collections.sort(items, new Comparator<OrderItem>() {

					@Override
					public int compare(OrderItem x, OrderItem y) {
						return cmp.compare(x.id, y.id);
					}
				});
				super.setItems(items);
			}
			
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item) {
				super.drawInternal(view, name, color, item);
				
				if (((OrderEx)doc.getData()).auto == 1){
					int c = ((OrderItemEx)item).qty != ((OrderItemEx)item).aqty ? R.color.item_highlight : R.color.black;
					int v = getResources().getColor(c);
					TextView tv = (TextView)view.findViewById(R.id.tvName);
					tv.setTextColor(v);
					tv = (TextView)view.findViewById(R.id.tvSum);
					tv.setTextColor(v);
					tv = (TextView)view.findViewById(R.id.tvQty);
					tv.setTextColor(v);
				}
			}
		});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itDebug) {
			AdapterView.AdapterContextMenuInfo menuInfo = 
					(AdapterContextMenuInfo) item.getMenuInfo();
				

			showDebug(menuInfo.position);
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	private void showDebug(int position) {
		OrderItemEx item = (OrderItemEx) lvItems.getAdapter().getItem(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Расчет позиции автозаказа");
		builder.setMessage(Html.fromHtml(item.debug));
		builder.create().show();
	}
}
