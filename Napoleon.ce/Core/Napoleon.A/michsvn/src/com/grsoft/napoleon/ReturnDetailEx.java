package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class ReturnDetailEx extends ReturnDetail {
	
	DeliveryList dlist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dlist = DeliveryList.open(doc.getId());
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		ReturnItem ri = (ReturnItem)item;
		int dqty = dlist.getItemQty(ri.date, ri.number, item.id);
		
		String qtyText;
		qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		qtyText += "<br><b>" + Util.IntToScaleStr(dqty, Consts.QTY_SCALE) + "</b>";
		tvQty.setText(Html.fromHtml(qtyText));
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
}
