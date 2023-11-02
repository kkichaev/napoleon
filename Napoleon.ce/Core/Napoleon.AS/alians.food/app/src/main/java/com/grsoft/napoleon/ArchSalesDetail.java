package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;


public class ArchSalesDetail extends SalesDetailEx {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, ArchSalesDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);
	}
	
	@Override protected void setSalesDoc(){ DocType.setCurDoc(ArchSalesDoc.instance()); } 
}
