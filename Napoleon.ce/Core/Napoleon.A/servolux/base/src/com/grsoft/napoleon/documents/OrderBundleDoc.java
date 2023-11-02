package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderBundleImpl;
import com.grsoft.napoleon.R;

public class OrderBundleDoc extends DocType {
	static OrderBundleDoc instance = null;
	
	OrderBundleDoc() {
		super("Заявка", "OrderBundle", OrderBundleImpl.class);
	}
	
	public static OrderBundleDoc instance() {
		if(instance == null)
			instance = new OrderBundleDoc();
		return instance;
	}

	@Override
	public int getResurceId() {
		return R.drawable.order_doc;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.order_doc_title;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.order_doc_2;
	}
}
