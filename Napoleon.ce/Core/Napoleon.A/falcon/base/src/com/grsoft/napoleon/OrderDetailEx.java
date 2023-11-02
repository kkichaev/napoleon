package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setVisibility(View.GONE);
	}

	@Override
	protected void updateTotalSum() {
		super.updateTotalSum();

		OrderEx oe = (OrderEx) doc.getData();
		OrgDogovor dog = DocHelper.getDogovor((OrgEx)org.getData(), oe.iddog);

//		int sum = doc.sum();
//		sum -= (int) (((long) sum * oe.getDiscval() + Consts.SUM_SCALE
//				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

		TextView tv;
//		tv = (TextView) findViewById(R.id.tvInfo);
//		tv.setText(String.format("%s(%s%%)",
//				Util.IntToScaleStr(sum, Consts.SUM_SCALE),
//				Util.IntToScaleStr(oe.getDiscval(), Consts.SUM_SCALE)));

		if( dog != null ) {
			tv = (TextView) findViewById(R.id.tvMinSum);
			tv.setText(getString(R.string.min_order_cost, Util.IntToScaleStr(
					dog.minOrder, Consts.SUM_SCALE)));
		}
	}
}
