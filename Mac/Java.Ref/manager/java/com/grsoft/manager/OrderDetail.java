package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.MOrderImplBase;
import com.grsoft.manager.documents.MOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class OrderDetail extends DocDetail {
	public static Class<? extends OrderDetail> activity = OrderDetail.class;
	
	public static void open(Context context, MOrderImplBase<? extends Order> doc) {
		Intent intent = new Intent(context, activity);
		
		intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
		intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());
		
		context.startActivity(intent);
	}
	
	@Override
	public String getTitle(CreateDocDataObject exdata) {	return getString(MOrderDoc.instance().getDocTitle()); }
	
	@Override
	public ListAdapter createAdapter() {
		return new BaseAdapter(){

			@SuppressWarnings("unchecked")
			@Override
			public int getCount() {	return ((MOrderImplBase<? extends Order>)getDocument()).getData().items.size();}

			@SuppressWarnings("unchecked")
			@Override
			public Object getItem(int pos) { return ((MOrderImplBase<? extends Order>)getDocument()).getData().items.get(pos); }

			@Override
			public long getItemId(int pos) { return 0; }

			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				OrderItem item = (OrderItem)getItem(pos);
				
				if(item != null)
					view = getItemView(view, item);
		
				view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
						: R.drawable.even_row_selector);
				
				return view;
			}
		};
	}
	
	private View getItemView(View view, OrderItem item){
		if(view == null)
			view = View.inflate(this, R.layout.docitems_row, null);
		
		((TextView) view.findViewById(R.id.tvName)).setText(priceName(item.id));
		((TextView) view.findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(
				item.qty, Consts.QTY_SCALE));
		((TextView) view.findViewById(R.id.tvCost)).setText(Util.IntToScaleStr(
				item.cost, Consts.SUM_SCALE));
		
		return view;
	}
}
