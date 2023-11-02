package com.grsoft.database;

import android.content.Context;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderDecision;


public class OrderDecisionHitching extends Hitching {
	public OrderDecisionHitching(Context context) {
		super(OrderDecision.class, DataObjectInfo.getInstance().getSrvName(OrderDecision.class));
	}
}
