package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	private TextView tvInfo;
	private boolean isAllowToBack = true;
	private int debet = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvInfo = (TextView) findViewById(R.id.tvInfo);
		DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(org.getData().id);
		debet = deliveryInfo.sum;
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

		if (((OrgEx) org.getData()).limitsum > 0 && DocType.getCurDoc() == OrderDoc.instance()) {
			if (debet + s >= ((OrgEx) org.getData()).limitsum) {
				tvInfo.setText(Html.fromHtml(getString(R.string.order_creation_reject,
						Util.IntToScaleStr(((OrgEx) org.getData()).limitsum, Consts.SUM_SCALE),
						Util.IntToScaleStr(debet, Consts.SUM_SCALE),
						Util.IntToScaleStr(((OrgEx) org.getData()).limitsum - debet, Consts.SUM_SCALE),
						Util.IntToScaleStr(s - (((OrgEx) org.getData()).limitsum - debet), Consts.SUM_SCALE)
						)));
				tvInfo.setVisibility(View.VISIBLE);
				isAllowToBack = false;
				btnSend.setEnabled(false);
			}
		}
	}
}
