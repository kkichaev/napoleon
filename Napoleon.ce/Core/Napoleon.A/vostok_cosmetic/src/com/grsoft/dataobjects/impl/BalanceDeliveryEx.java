package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.documents.BalanceDelivery;

public class BalanceDeliveryEx extends BalanceDelivery {
	@Override
	public String getDescription(Context context) { 
		return data.number + " " + ((DeliveryEx)data).agent;
	}
}
