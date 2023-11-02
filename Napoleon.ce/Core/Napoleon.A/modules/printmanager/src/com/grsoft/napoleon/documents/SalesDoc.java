package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.R;

public class SalesDoc extends OrderDoc {

	static public final String DOC_NAME = "Продажи";
	static public final String OBJ_NAME = "Sales";
	protected static DocType instance;
	
	protected SalesDoc() {
		super(DOC_NAME, OBJ_NAME, SalesImpl.class);
	}
	
	protected SalesDoc(Class<? extends OrderImplBase<? extends Order>> type) {
		super(DOC_NAME, OBJ_NAME, type);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new SalesDoc();
		return instance;
	}
	
	public static DocType instance(Class<? extends OrderImplBase<? extends Order>> docType) {
		if( instance == null )
			instance = new SalesDoc(docType);
		return instance;
	}
	
	protected SalesDoc(String name, String objName,   Class<? extends OrderImplBase<? extends Order>> type) {
		super(name, objName, type);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.sales;
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		super.refreshDocSum(orgId);
		DebtDoc.instance().refreshDocSum(orgId);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.sales_doc_title;
	}
	
	@Override
	public int getResurce2Id() {
		return getResurceId();
	}
}
