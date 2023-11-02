package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class WSOrderDetail extends OrderDetail {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	private DocType docType = OrderDoc.instance();
	protected void onCreate(android.os.Bundle savedInstanceState) {
		docType = DocType.getCurDoc();
		DocType.setCurDoc(WSOrderDoc.instance());
		super.onCreate(savedInstanceState);
	};
	
	@Override
	protected void onDestroy() {
		DocType.setCurDoc(docType);
		super.onDestroy();
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			PriceImpl pi = new PriceImpl();
			
			@Override
			int getResourceID() { return R.layout.wsorderdetail_list_row; }
			
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
				super.drawInternal(view, name, color, item, pos);
				
				TextView tv = (TextView) view.findViewById(R.id.tvQty);
				pi.read("id", item.id);
				tv.setText(Util.IntToScaleStr(doc.getItemValue(pi.getData()), Consts.QTY_SCALE));
			}
		});
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}
}
