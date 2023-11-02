package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.napoleon.documents.DShipmentDoc;

import android.content.Context;

public class DispatchImplEx extends DispatchImpl {
	@Override
	public boolean isDocFinished(Context context) {
		for(int i=0; i<data.items.size(); i++) {
			DispatchItem di = data.items.get(i);
			if(di.type.equals(DShipmentDoc.instance().getObjectName()))
				return isItemFinished(context, i);
		}
		
		return super.isDocFinished(context);
	}
}
