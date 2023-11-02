package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImplBase;
import com.grsoft.dataobjects.impl.ISReturnImpl;
import com.grsoft.util.ExtrasConst;

public class ISReturnDetail extends DeliveryDetail {
	static public void open(Context context, DbObject<?> doc) {
		Intent i = new Intent(context, ISReturnDetail.class);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	DeliveryImplBase<? extends Delivery> createDelivery() {
		return new ISReturnImpl();
	}
}
