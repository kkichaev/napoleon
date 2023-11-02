package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DlvReturn;
import com.grsoft.napoleon.DlvReturnDetail;
import android.content.Context;

public class DlvReturnImpl extends DeliveryImplBase<DlvReturn>{

	@Override
	public void open(Context context) {
		DlvReturnDetail.open(context, this);
	}

}
