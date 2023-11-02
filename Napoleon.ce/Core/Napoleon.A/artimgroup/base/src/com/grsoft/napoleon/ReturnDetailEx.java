package com.grsoft.napoleon;

import java.util.HashMap;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.util.Util;

public class ReturnDetailEx extends ReturnDetail {
	
	HashMap<String, DiscountItem> discounts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ReturnEx re = (ReturnEx) doc.getData();
		discounts = DiscountImpl.loadFromDogovor(re.iddog);
		
		btnLines.setVisibility(View.GONE);
		if( !linesController.isVariable() )
			linesController.setVariable();
		
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			
			super.drawInternal(view, name, color, item);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);

			ReturnItem ri = (ReturnItem)item; 
			String text = name;
			if( ri.number.length() > 0 ) {
				DiscountItem disc = discounts.get(ri.discid);
				
				text += "<br><b>" + ri.number + "</b> " + Util.simpleDateFormat.format(ri.date);
				if( disc != null ) {
					text += " <i>" + disc.name + "</i>";
				}
			}
			tvName.setText(Html.fromHtml(text));
			tvName.setTextColor(color);
		}
	}
}
