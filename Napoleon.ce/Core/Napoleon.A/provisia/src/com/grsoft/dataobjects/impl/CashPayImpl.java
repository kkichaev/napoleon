package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.CashPay;
import com.grsoft.napoleon.CashPayEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class CashPayImpl extends CreatableDocument<CashPay> {

	@Override
	public void open(Context context) {
		CashPayEdit.open(context, this);
	}

	@Override
	public int sum() {
		return data.sum;
	}
}
