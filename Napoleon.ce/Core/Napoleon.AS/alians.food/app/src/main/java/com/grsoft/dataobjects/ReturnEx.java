package com.grsoft.dataobjects;

import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class ReturnEx extends Return {
	public String dogovor;
	public String ido;
	public String costCode;
	
	public long sum() {
		int result = 0;
		for (OrderItem orderItem: items)
			result += FPOperation.itemMul(orderItem.cost, orderItem.qty, Consts.QTY_SCALE);
		return result;
	}
}
