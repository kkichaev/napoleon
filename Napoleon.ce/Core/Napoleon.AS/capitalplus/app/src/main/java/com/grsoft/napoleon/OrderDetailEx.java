package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;

public class OrderDetailEx extends OrderDetail {
	private TextView tvInfo;
	private boolean isAllowToBack = true;
	DeliveryInfo deliveryInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvInfo = (TextView) findViewById(R.id.tvInfo);
		deliveryInfo = DeliveryInfo.collectDelivery(org.getData().id);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}

	@Override
	public void onBackPressed() {
		if(isAllowToBack || doc.sum() == 0)
			super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		long s = doc.sum();

		tvInfo.setVisibility(View.GONE);
		isAllowToBack = true;
		btnSend.setEnabled(true);

		if (doc.getData() instanceof OrderEx) {
			if (((OrgEx) org.getData()).limitsum > 0 && deliveryInfo.count > 0 && (deliveryInfo.sum + s > ((OrgEx) org.getData()).limitsum)) {
				tvInfo.setText(getString(R.string.order_creation_reject));
				tvInfo.setVisibility(View.VISIBLE);
				isAllowToBack = false;
				btnSend.setEnabled(false);
			}
		}
	}
}
