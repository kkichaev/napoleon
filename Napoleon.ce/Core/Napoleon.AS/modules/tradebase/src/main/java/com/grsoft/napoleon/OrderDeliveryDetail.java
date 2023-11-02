package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/***
 * Заявка - накладная
 * @author kki
 *
 */
public class OrderDeliveryDetail extends OrderDetail {
	public static Class<? extends Activity> activity = OrderDeliveryDetail.class;
	List<DeliveryItem> items = new ArrayList<DeliveryItem>();

	long sum = 0;
	int weight = 0;
	int count = 0, countPack = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void init() {
		loadItems();
		updateTotals();
	}

	protected void loadItems() {
		String where = "created=" + Long.toString(doc.getData().created.getTime());
		DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>() {

			@Override
			public boolean travel(DataTraveler<Delivery> item) {
				items.addAll(item.data.items);
				return true;
			}
		}, where);

	}

	protected void updateTotals() {
		final PriceImpl pi = new PriceImpl();
		final Price p = pi.getData();

		for(DeliveryItem di : items) {
			sum += di.sum;

			count += di.qty;

			p.id = di.id;
			if(pi.read()) {
				int inPack = p.qtyInPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				countPack += (int)((long)di.qty * Consts.QTY_SCALE / inPack);
				weight += (int)((long)di.qty * p.weight / Consts.QTY_SCALE);
			}
		}

		pi.close();
		count /= Consts.QTY_SCALE;
		countPack /= Consts.QTY_SCALE;
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdeliverydetail);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderDeliveryItemsAdapter());
	}
	
	static public void open(Context context, OrderImpl order) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void updateTotalSum() {
		updateTotalSum(sum, weight, ((CfgNplW)ConfigManager.getConfig()).isPackView ? countPack : count);
	}
	
	protected DeliveryItem getDlvItem(String id) {
		for( DeliveryItem i : items ) {
			if( i.id.equals(id) )
				return i;
		}
		
		return null;
	}
	
	protected void drawDeliveryItem(int color, DeliveryItem ditem, OrderItem oitem, TextView tvQty) {
		boolean showPack = (oitem.inPack() && ((CfgNplW)ConfigManager.getConfig()).isPackView);
		String qtyText;

		int qty = ditem == null ? 0 : ditem.qty;
		
		if( !showPack )
			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
		else {
			Price p = price.getData();
			int inPack = p.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			qty = (int)((long)qty * Consts.QTY_SCALE / inPack);
			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " у.";
		}
		
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setText(qtyText);
		tvQty.setTextColor(color);
	}
	
	protected int getItemLayoitId(){ return R.layout.orderdeliverydetail_list_row; }
	protected void drawItem(View view, DeliveryItem dlvItem, OrderItem ordItem, int color ){ }
	
	class OrderDeliveryItemsAdapter extends OrderItemsAdapter{
	
		public OrderDeliveryItemsAdapter() {}
		
		@Override
		int getResourceID() { return getItemLayoitId(); }
		
		DeliveryItem currentItem;
		
		@Override
		protected long getItemSum(OrderItem item) {
			return (currentItem == null) ? 0 : currentItem.sum;
		}
		
		protected int getItemColor(OrderItem item, int defaultColor) {
			int color = defaultColor;
			
			int qty = (currentItem == null) ? 0 : currentItem.qty; 
			if( qty != item.qty )
				color = Color.RED;
			
			return color;
		}
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			currentItem = getDlvItem(item.id);

			TextView tvDispatch = (TextView) view.findViewById(R.id.tvDispatch);
//			int qty = (currentItem == null) ? 0 : currentItem.qty;
			color = getItemColor(item, color);
			
			super.drawInternal(view, name, color, item, pos);
			
			drawDeliveryItem(color, currentItem, item, tvDispatch);
			drawItem(view, currentItem, item, color);
//			boolean showPack = (item.inPack() && ((CfgNplW)ConfigManager.getConfig()).isPackView);
//			String qtyText;
//
//			if( !showPack )
//				qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
//			else {
//				Price p = price.getData();
//				int inPack = p.qtyInPack;
//				if( inPack == 0 )
//					inPack = Consts.QTY_SCALE;
//				qty = (int)((long)qty * Consts.QTY_SCALE / inPack);
//				qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " у.";
//			}
//			
//			TextView tvDispatch = (TextView) view.findViewById(R.id.tvDispatch);
//			tvDispatch.setGravity(Gravity.RIGHT);
//			tvDispatch.setText(qtyText);
//			tvDispatch.setTextColor(color);
		}
	}
}
