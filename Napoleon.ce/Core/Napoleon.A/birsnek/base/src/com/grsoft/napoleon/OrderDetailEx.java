package com.grsoft.napoleon;

public class OrderDetailEx extends OrderDetail {

	@Override
	protected void doUnsettedFocus() {
		doc.delete();
	}
}
