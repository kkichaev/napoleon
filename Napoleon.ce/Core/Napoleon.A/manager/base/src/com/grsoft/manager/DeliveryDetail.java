package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.MDeliveryImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class DeliveryDetail extends DocDetail {
	public static void open(Context context, Document<?> doc) {
		Intent intent = new Intent(context, DeliveryDetail.class);
		
		intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
		intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());
		
		context.startActivity(intent);
	}
	
	@Override
	public ListAdapter createAdapter() {
		return new BaseAdapter(){

			@Override
			public int getCount() {	return ((MDeliveryImpl)getDocument()).getData().items.size();}

			@Override
			public Object getItem(int pos) { return ((MDeliveryImpl)getDocument()).getData().items.get(pos); }

			@Override
			public long getItemId(int pos) { return 0; }

			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				DeliveryItem item = (DeliveryItem)getItem(pos);
				
				if(item != null)
					view = getItemView(view, item);
		
				view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
						: R.drawable.even_row_selector);
				
				return view;
			}
		};
	}
	
	private View getItemView(View view, DeliveryItem item){
		if(view == null)
			view = View.inflate(this, R.layout.docitems_row, null);
		
		((TextView) view.findViewById(R.id.tvName)).setText(priceName(item.id));
		((TextView) view.findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(
				item.qty, Consts.QTY_SCALE));
		((TextView) view.findViewById(R.id.tvCost)).setText(Util.IntToScaleStr(
				item.sum, Consts.SUM_SCALE));
		
		return view;
	}
}
