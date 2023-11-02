package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ActDelivery;
import com.grsoft.napoleon.ActDeliveryDetail;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;

public class ActDeliveryImpl extends DeliveryImplBase<ActDelivery> {
	@Override public void open(Context context) { 
		Intent i = new Intent(context, ActDeliveryDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, getRowid());
		context.startActivity(i);
	}
}
