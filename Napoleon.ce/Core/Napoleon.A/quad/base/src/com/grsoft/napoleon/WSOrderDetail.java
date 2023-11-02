package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.ExtrasConst;

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
			@Override
			int getResourceID() {
				return R.layout.wsorderdetail_list_row;
			}
		});
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}
}
