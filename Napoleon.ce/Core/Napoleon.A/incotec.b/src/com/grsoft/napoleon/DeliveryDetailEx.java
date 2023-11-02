package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.DeliveryEx;

import android.os.Bundle;
import android.widget.TextView;

public class DeliveryDetailEx extends DeliveryDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DeliveryEx de = (DeliveryEx) delivery.getData();
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		String text = tvOrg.getText().toString();
		
		long diff = ((new Date()).getTime() - de.payDate.getTime()) / (1000 * 3600 * 24);
		if(diff > 0) {
			text += "\nПросрочено " + Long.toString(diff) + " дней";
			tvOrg.setText(text);
		}
	}
}
