package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderW;
import com.grsoft.dataobjects.impl.OrderWImpl;
import com.grsoft.napoleon.R;

public class OrderWDoc extends OrderDoc{
	static protected OrderWDoc instance = null;
	static public final String DOC_NAME = "Заявки покупателя";
	static public final String OBJ_NAME = "OrderW";
	
	protected OrderWDoc() { super(DOC_NAME, OBJ_NAME, OrderWImpl.class);} 

	static public DocType instance() {
		if( instance == null ){
			DataObjectInfo doi = DataObjectInfo.getInstance(); 
			doi.replaceTableName(OrderW.class, OBJ_NAME);
			
			instance = new OrderWDoc();
		}
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.orderw;
	}
}
