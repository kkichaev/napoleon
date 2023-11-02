package com.grsoft.database;

import java.util.List;

import com.grsoft.dataobjects.DlvMove;
import com.grsoft.dataobjects.DlvMoveItem;
import com.grsoft.dataobjects.impl.DlvMoveImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DlvMoveDayHitching extends Hitching {
	
	DlvMoveItemHandler handler = new DlvMoveItemHandler();	
	DlvMoveImpl dlv = new DlvMoveImpl();
	
	public DlvMoveDayHitching() {
		super(DlvMove.class, "DailyDeliveryBalanceData");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		handler.onStart();
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		dlv.close();
		handler.onEnd();
	}
	
	
	boolean haveItem(List<DlvMoveItem> items, DlvMoveItem item) {
		for(DlvMoveItem i : items) {
			if(i.type.equals(item.type) && i.num.equals(item.num)) {
				i.sum = item.sum;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DlvMove check = dlv.getData();
		DlvMove dobj = (DlvMove)rawObject.createDataObject(dataObject);

		for(DlvMoveItem di : dobj.items) {
			handler.onStep(di);
		}
		
		check.num = dobj.num;
		check.id = dobj.id;
		if( dlv.read() ) {
			for(DlvMoveItem di : dobj.items) {
				if( haveItem(check.items, di) == false )
					check.items.add(di);
			}
			dlv.write();
		} else 
			dbProxy.insertRecord(dobj);
	}
}
