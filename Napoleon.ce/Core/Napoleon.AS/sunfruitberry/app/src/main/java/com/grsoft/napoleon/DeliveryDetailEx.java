package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryReturnItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeliveryDetailEx extends DeliveryDetail {
	long retSum = 0;

	@Override protected int getContentViewId() { return R.layout.orderdetailex; }
	
	@Override protected DeliveryItemsAdapter createItemsAdapter() { return new Adapter(); }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void readDocument(long rowid) {
		super.readDocument(rowid);
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		List<DeliveryReturnItem> items = new ArrayList<DeliveryReturnItem>();
		for(DeliveryItem di : delivery.getData().items) {
			p.id = di.id;
			pi.read();

			DeliveryReturnItem dri = new DeliveryReturnItem();
			dri.id = di.id;
			dri.name = p.name.toUpperCase();
			items.add(dri);

			dri.sum = di.sum;
			if(di.qty > 0) {
				dri.qty = di.qty;
			}
			else {
				dri.returnQty = -di.qty;
				retSum += dri.sum;
			}
		}
		pi.close();
		
		Collections.sort(items);
		
		delivery.getData().items.clear();
		delivery.getData().items.addAll(items);
	}
	
	@Override
	public void updateTotalSum(long sum, int weight) {
		String s = DeliveryDoc.instance().weightToString(weight, getString(R.string.kg));
		s += "<br/>";
		if(retSum > 0) {
			s += Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			s += "&nbsp;&nbsp;<font color='red'>" + Util.IntToScaleStr(retSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font>&nbsp;&nbsp;";
		}
		s += "<b>" + Util.IntToScaleStr(sum - retSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		
		TextView tv = (TextView)findViewById(R.id.tvTotalSum);
		tv.setText(Html.fromHtml(s));
	}
	
	class Adapter extends DeliveryItemsAdapter {
		@Override protected int getLayoutID() { return R.layout.orderdetail_row_ex; }
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);
			
			TextView tv = (TextView)v.findViewById(R.id.tvCost);
			DeliveryReturnItem i = (DeliveryReturnItem) getItem(arg0);
			int cost = i.qty == 0 && i.returnQty == 0 ? 0 : (int)((long)i.sum * Consts.QTY_SCALE / (i.qty + i.returnQty));
			tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			int color = i.returnQty > 0 ? Color.RED : Color.BLACK;
			if(i.returnQty > 0) {
				String text = Util.IntToScaleStr(i.returnQty, Consts.QTY_SCALE);
				((TextView)v.findViewById(R.id.tvQty)).setText(text);
			} else {
			}
			
			for(int id : new int[] {R.id.tvName, R.id.tvQty, R.id.tvSum, R.id.tvCost }) {
				((TextView)v.findViewById(id)).setTextColor(color);
			}

			return v;
		}
	}
}	
