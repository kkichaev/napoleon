package com.grsoft.napoleon;

import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.OrderEx;
import android.view.View;
import android.widget.EditText;



public class CarrierDeliveryFromWh extends SelfDelivery {
	private EditText edDlvInfo;
	
	@Override
	protected int getLayoutID() { return R.layout.carrier_delivery_from_wh;	}
	
	@Override
	protected void inflateView(View view) {
		super.inflateView(view);
		edDlvInfo = (EditText) view.findViewById(R.id.edDlvInfo);
	}
	
	@Override
	protected void initView() {
		super.initView();
		edDlvInfo.setText(((OrderEx)order.getData()).dlvinfo);
	}
	
	@Override
	public boolean checkAndUpdate(IOrder order) {
		boolean result = super.checkAndUpdate(order);
		
		if(result)
			order.setDlvInfo(edDlvInfo.getText().toString().trim());
		
		return result;
	}
}
