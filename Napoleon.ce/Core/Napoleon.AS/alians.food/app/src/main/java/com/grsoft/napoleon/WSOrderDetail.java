package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.WSAddOrderImpl;
import com.grsoft.napoleon.OrderDetail.ItemsOnClickListener;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSAddOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WSOrderDetail extends OrderDetail {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
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
				
				TextView tv = (TextView) view.findViewById(R.id.tvVanQty);
				pi.read("id", item.id);
				tv.setText(Util.IntToScaleStr(doc.getItemValue(pi.getData()), Consts.QTY_SCALE));
				
				if (DocType.getCurDoc() == WSAddOrderDoc.instance()) {
					tv.setTag(item.id);
					//tv.setOnClickListener(WSOrderDetail.this);
				}
			}
		});
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (DocType.getCurDoc() == WSAddOrderDoc.instance()) {
			TextView tv = (TextView) findViewById(R.id.tvSource);
			tv.setText("Остатки");
		}
	}
	
	protected void editItem(OrderItem orderItem) {
		if (DocType.getCurDoc() == WSAddOrderDoc.instance()) {
			if( Features.HAVE_PRICE_MOVER )
				PriceCount.PriceMover = new OrderPriceMover(doc);
		
			PriceImpl pi = new PriceImpl();
			pi.getData().id = orderItem.id;
			pi.read();
			pi.close();
			((WSAddOrderImpl)doc).editItemMode(pi.getRowid(), this);
		}else
			super.editItem(orderItem);
	}
}
