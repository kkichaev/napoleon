package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl2Ex;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrderDetail2Ex extends OrderDetailEx {
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			int getResourceID() { return R.layout.orderdetail_list_rowex; }
			
			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				view = super.getView(pos, view, arg2);
				
				if (DocType.getCurDoc() == OrderDoc.instance()) {
					OrderItem item = (OrderItem) getItem(pos);
					long ren = ((OrderImpl2Ex)doc).getRentability(item.id);
					TextView tv = (TextView) view.findViewById(R.id.tvRen);
					tv.setText(Util.IntToScaleStr(ren, Consts.SUM_SCALE));
				}
				
				return view;
			}
		});
	}
	
	
}
