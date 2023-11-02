package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.manager.DeliveryDetail;
import com.grsoft.napoleon.documents.Document;


public class MDeliveryImpl extends Document<Delivery>{

	@Override
	public void open(Context context) { DeliveryDetail.open(context, this); }
	
	@Override public String getDescription(Context context) { return data.number; }

}
