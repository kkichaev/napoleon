package com.grsoft.database;

import android.util.Log;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class ReturnImportHitching extends Hitching {
	PriceImpl pi = new PriceImpl();
	CostStrategy cs;
	
	public ReturnImportHitching() {
		super(ReturnDoc.instance().dataType(), "ReturnsImported");
		
		cs = CostStrategy.defaultInstance;
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Log.d(Consts.D_TAG, "ReturnImportHitching.onRead");
		DataObject dobj = rawObject.createDataObject(ReturnDoc.instance().dataType());
		((Return)dobj).params |= ParamState.ofExported;
		
		Price p = pi.getData();
		for(OrderItem i : ((Return)dobj).items) {
			p.id = i.id;
			pi.read();
			i.cost = cs.getItemCost(p, null);
			i.flags |= OrderItem.IN_PACK;
		}
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		pi.close();
		try{
			ReturnDoc.instance().refreshDocSum();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
