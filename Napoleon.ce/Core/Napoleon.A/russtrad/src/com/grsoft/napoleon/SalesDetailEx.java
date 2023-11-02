package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.util.Util;

public class SalesDetailEx extends SalesDetail {

	class SalesItemsAdapter extends OrderItemsAdapter{
		@Override
		protected void drawInternal(View view, String name, int color,
				OrderItem item) {
			super.drawInternal(view, name, color, item);
			
			SalesItem salesItem = (SalesItem)item;
			
			if(salesItem != null){
				TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
				tvQty.setText(Html.fromHtml(
						String.format("%s<br>%s", 
								tvQty.getText().toString(), 
								Util.simpleDateFormat.format(salesItem.date))));
			}
		}
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new SalesItemsAdapter());
	}
}
