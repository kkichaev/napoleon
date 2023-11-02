package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.ExtrasConst;

public class OrderImplEx extends OrderImpl {

	public long insert() {
		rowid = ExtrasConst.INVALID_ROWID;
		write();
		close();
		return rowid;
	}
}
