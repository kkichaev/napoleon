package com.grsoft.database;

import android.util.Log;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class OrderImportedHitching extends Hitching {

	public OrderImportedHitching() {
		super(OrderDoc.instance().dataType(), "OrderImported");
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Log.d(Consts.D_TAG, "OrderImportedHitching.onRead");
		DataObject dobj = rawObject.createDataObject(OrderDoc.instance().dataType());
		((Order)dobj).params |= ParamState.ofExported;
		
		for(OrderItem item: ((Order)dobj).items)
			item.flags |= OrderItem.IN_PACK;
		
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		try{
			OrderDoc.instance().refreshDocSum();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
