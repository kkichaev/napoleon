package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.ReturnImplEx;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ReturnDetailEx extends ReturnDetail {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (doc.isEditable())
			getMenuInflater().inflate(R.menu.return_detail_context_ment, menu);
	}
	
	@Override
	protected void deleteItem(OrderItem orderItem) {
		((ReturnImplEx)doc).updateQty(orderItem, 0, 0, false);
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
		updateTotalSum();
	}
	
	@Override
	protected void editItem(OrderItem orderItem) {
		((ReturnImplEx)doc).editItem(orderItem, this);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itNew) {
			OrderItem orderItem = (OrderItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
			ReturnItem ri = new ReturnItem();
			ri.id = orderItem.id;
			ri.cost = orderItem.cost;
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.set(1900, 0, 1);
			ri.prdDate= c.getTime();
			doc.getData().items.add(ri);
			doc.write();
			editItem(ri);
			return true;
		}
		return super.onContextItemSelected(item);
	}
}
