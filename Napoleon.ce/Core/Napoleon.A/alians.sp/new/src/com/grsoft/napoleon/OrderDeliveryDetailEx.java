package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.Set;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override protected void setContentView() { setContentView(R.layout.orderdeliverydetailex); }
	@Override protected int getItemLayoitId() { return R.layout.orderdeliverydetail_list_rowex;}
	
	
	@Override
	protected void drawItem(View view, DeliveryItem dlvItem, OrderItem ordItem, int color) {
		super.drawItem(view, dlvItem, ordItem, color);
		
		TextView tv = (TextView) view.findViewById(R.id.tvDisc);
		tv.setText(dlvItem == null? "" : Util.IntToScaleStr(((DeliveryItemEx)dlvItem).disc, Consts.SUM_SCALE));
		tv.setTextColor(color);
	}
	
	@Override
	protected void updateTotalSum() {
		String s = DocType.getCurDoc().getTotalSumStr(this, delivery.sum(), delivery.weight(), 0);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<i>");
		sb.append(getString(R.string.sku_qty, SkuCount()));
		sb.append(",</i> ");
		sb.append(s);
		
		TextView tv = (TextView) findViewById(R.id.tvTotalSum);
		tv.setText(Html.fromHtml(sb.toString()));
	}
	
	private int SkuCount() {
		Set<String> set = new HashSet<String>();
		
		for(DeliveryItem i : delivery.getData().items)
			set.add(i.id);
		
		return set.size();
	}
}
