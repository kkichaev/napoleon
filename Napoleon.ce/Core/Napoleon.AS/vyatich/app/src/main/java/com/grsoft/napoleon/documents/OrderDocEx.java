package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Util;

public class OrderDocEx extends OrderDoc {
	public OrderDocEx(String docName, String objName, Class<? extends OrderImplBase<? extends Order>> type) {
		super(docName, objName, type);
	}

	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> type) {
		instance = new OrderDocEx("Заявки", "Order", type);
		return instance;
	}

	@Override
	public String weightToString(long weight, String kgStr) {
		return "\n" + Util.IntToScaleStr(weight, 10, Util.DEC_DELIM, false) +  " дал";
	}
}
