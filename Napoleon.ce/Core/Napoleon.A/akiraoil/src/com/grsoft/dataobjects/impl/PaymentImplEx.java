package com.grsoft.dataobjects.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.napoleon.PayData;

public class PaymentImplEx extends PaymentImpl {
	@Override
	public long sum() {

		PayData pd = new PayData();
		JsonElement root = new JsonParser().parse(((PaymentEx)data).json);
		pd.read(root);
		
		return pd.debetSum();
	}
}
