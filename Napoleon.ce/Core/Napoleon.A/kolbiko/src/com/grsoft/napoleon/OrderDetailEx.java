package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	
	PriceImpl pi = new PriceImpl();
	ReturnImpl retDoc = null;
	RemnantsImpl restDoc = null;
	
	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	@Override
	protected void onStop() {
		super.onStop();
		pi.close();
	}
	
	@Override
	protected void afterDocReaded() {
		retDoc = ReturnImplEx.getAssociated(doc, false);
		
		long r = RemnantsImpl.find(doc.getId(), doc.getData().created);
		if( r != ExtrasConst.INVALID_ID ) {
			restDoc = new RemnantsImpl();
			restDoc.read(r);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		int orderCount = (doc.getData().items != null ) ? doc.getData().items.size() : 0;
		int retCount = 0;
		int restCount = 0;
		
		if( retDoc != null )
			retCount = (retDoc.getData().items != null ) ? retDoc.getData().items.size() : 0;
		
		if( restDoc != null )
			restCount = (restDoc.getData().items != null ) ? restDoc.getData().items.size() : 0;

		TextView tv;
		tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(String.format("Заказ - %d / В витр - %d / Возврат - %d", orderCount, restCount, retCount));
	}
	
	@Override
	protected void updateTotalSum() {
		int sum = doc.sum();
		int weight = doc.weight();
		int retWeight = (retDoc != null) ? retDoc.weight() : 0;

		TextView tvTotalSum = (TextView)findViewById(R.id.tvTotalSum);		
		if (tvTotalSum != null)
		{
			String sumStr = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			String str;
			int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
			if( weight >  0 || retWeight > 0 ) {
				str = Integer.toString(weight) + " / " + Integer.toString(retWeight) + " кг";
				ii = 0;
				iie = str.length();
				ei += iie + 1;
				si += iie + 1;
				str += "\n" + sumStr;				
			} else
				str = sumStr;
			
			SpannableString ss = new SpannableString(str);
			if( ii >= 0 ) 
				ss.setSpan( new StyleSpan(android.graphics.Typeface.ITALIC), ii, iie, 0);			
			ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD), si, ei, 0);			
			tvTotalSum.setText(ss);//
		}
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItems());
}
	
	class OrderItems extends OrderItemsAdapter {
		
		@Override
		protected void setItems(List<OrderItem> iitems) {
			List<OrderItem> items = new ArrayList<OrderItem>(iitems);
			
			HashSet<String> used = new HashSet<String>();
			for(OrderItem i : items)
				used.add(i.id);
			
			if( retDoc != null)
				for(OrderItem i : retDoc.getData().items) {
					if(used.contains(i.id) == false) {
						OrderItem add = new OrderItem();
						add.id = i.id;
						items.add(add);
						used.add(i.id);
					}
				}

			if( restDoc != null)
				for(RemnantItem i : restDoc.getData().items) {
					if(used.contains(i.id) == false) {
						OrderItem add = new OrderItem();
						add.id = i.id;
						items.add(add);
					}
				}
			super.setItems(items);
		}
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);						
	
			linesController.prepareTextView(tvName);
			tvName.setText(name);
			tvName.setTextColor(color);
						
			int sum = FPOperation.itemMul(item.cost, item.qty, Consts.QTY_SCALE);
			String txt = "";
			txt += Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			txt += "\n";
			
			pi.getData().id = item.id;
			if( pi.read() ) {
				int w = (int)((long)item.qty * pi.getData().weight) / Consts.QTY_SCALE;
				txt += Util.IntToScaleStr(w, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false);
			}
			
			tvSum.setText(txt);
			tvSum.setGravity(Gravity.RIGHT);
			tvSum.setTextColor(color);
			linesController.prepareTextView(tvSum);
			
			txt = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			txt += "\n";
			
			int rest = 0, rets = 0;
			if( restDoc != null ) {
				RemnantItem ri = (RemnantItem)restDoc.findItem(item.id);
				if( ri != null )
					rest = ri.qty;
					
			}
			if( retDoc != null ) {
				OrderItem oi = (OrderItem)retDoc.findItem(item.id);
				if( oi != null )
					rets = oi.qty;
			}
			
			txt += Util.IntToScaleStr(rest, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			txt += " / ";
			txt += Util.IntToScaleStr(rets, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			tvQty.setText(txt);
			tvQty.setGravity(Gravity.RIGHT);
			tvQty.setTextColor(color);
			linesController.prepareTextView(tvQty);
		}
	}
}
