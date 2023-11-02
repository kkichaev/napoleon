package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void init() {
		btnSend.setEnabled(((OrderEx)doc.getData()).fromKIS == 0);
	}

}
