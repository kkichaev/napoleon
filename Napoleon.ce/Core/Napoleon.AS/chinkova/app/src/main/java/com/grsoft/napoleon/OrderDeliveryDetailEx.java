package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override protected void setContentView() { setContentView(R.layout.orderdeliverydetailex); }

	@Override
	protected void onResume() {
		super.onResume();
		OrderDetailEx.updatePrcInfo(this, (OrderEx) doc.getData());
	}
}
