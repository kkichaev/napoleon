package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.QualityItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.QualityImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;

public class ReturnDetailEx extends OrderDetailEx {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, ReturnDetailEx.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override protected boolean haveFocusedGroup() { return false; }
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapterExEx());
	}
	
	@Override
	boolean haveCompleeteFocusedItems() {
		return true;
	}
	
	class OrderItemsAdapterExEx extends OrderItemsAdapterEx{
		@Override
		protected void drawInternal(View view, String name, int color,
				OrderItem item) {
			super.drawInternal(view, name, color, item);
			
			TextView tvQuality = (TextView) view.findViewById(R.id.tvQuality);
			QualityItem qualityItem = QualityImpl.getItem(((OrderItemEx)item).cause);
			
			if (qualityItem == null || qualityItem.name.length() == 0)
				tvQuality.setVisibility(View.GONE);
			else
				tvQuality.setText(Html.fromHtml("<i>" + qualityItem.name + "</i>")) ;
		}
		
		@Override
		int getResourceID() {
			return R.layout.orderdetail_list_row_ex;
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		((ReturnImplEx)doc).editItemId = ((AdapterContextMenuInfo)
				item.getMenuInfo()).position;
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected ItemsOnClickListener createItemsCL() {
		return new ItemsOnClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				((ReturnImplEx)doc).editItemId = pos;
				super.onItemClick(arg0, arg1, pos, arg3);
			}
		};
	}
}
