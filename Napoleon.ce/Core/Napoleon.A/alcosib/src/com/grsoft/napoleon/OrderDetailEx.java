package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);
		tvTotalSum.setVisibility(View.GONE);
	}
	
	protected void setContentView(){
		setContentView(R.layout.orderdetailex);
	}

	@Override
	protected void updateTotalSum() {

		if (doc instanceof OrderImplEx) {
			long sum = doc.sum();
			int count = doc.count();
			int weight = doc.weight();
			int cubature = ((OrderImplEx)doc).cubature();
			
			((TextView)findViewById(R.id.tvSumSum)).setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE,
					Util.DEC_DELIM, false));
			((TextView)findViewById(R.id.tvQtySum)).setText(Integer.toString(count) + getString(R.string.sht));
			((TextView)findViewById(R.id.tvCubSum)).setText(Util.IntToScaleStr(cubature, 100000,
					Util.DEC_DELIM, false));
			((TextView)findViewById(R.id.tvWeightSum)).setText(Integer.toString(
					(weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE) + getString(R.string.kg));
		} else
			super.updateTotalSum();
	}
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapterEx());
	}
	
	class OrderItemsAdapterEx extends OrderItemsAdapter {
		int getResourceID() { return R.layout.orderdetail_list_rowex; }
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			String name;
			OrderItem item = (OrderItem) getItem(arg0);
			int cub = 0;
			PriceEx p = (PriceEx)price.getData();
			p.id = item.id;
			if( price.read() ){
				name = p.name;
				cub = p.cubature;
			}else
				name = "< " + getString(R.string.id) + " '" + item.id + "' >";
			
			View view = arg1; 			
			if (view == null)
				view = View.inflate(OrderDetailEx.this, getResourceID(), null);
			
			view.setTag(item);
			view.findViewById(R.id.tvName).setTag(item.id);
			
			
			drawInternalEx(view, name, Color.BLACK, item, cub);			
			return view;
		}
		
		protected void drawInternalEx(View view, String name, int color, OrderItem item, int cub) {
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);						
			TextView tvCub = (TextView)view.findViewById(R.id.tvCubature);
			
			tvCub.setText(Util.IntToScaleStr(cub, 100000,
							Util.DEC_DELIM, true));
			
			linesController.prepareTextView(tvName);
			tvName.setText(name);
			tvName.setTextColor(color);
						
			DataObjectInfo dataObjectInfo = DataObjectInfo.getInstance();
			
			int costScale = dataObjectInfo.getScale(OrderItem.class, "cost");
			long sum = getItemSum(item);
			tvSum.setText(Util.IntToScaleWStr(sum, costScale, Consts.PRICE_DEC_WIDTH, false));
			tvSum.setGravity(Gravity.RIGHT);
			tvSum.setTextColor(color);
			
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			drawItemQty(color, item, tvQty);
		
			if( Features.SHOW_NUMBER_IN_ORDER ) {
				TextView tv; 
				tv = (TextView)view.findViewById(R.id.tvOrder);
				if( tv != null ) {
					tv.setVisibility(View.VISIBLE);
					int id = doc.getData().items.indexOf(item) + 1;
					tv.setText(Integer.toString(id));
				}
			}			
		}
	}
}
