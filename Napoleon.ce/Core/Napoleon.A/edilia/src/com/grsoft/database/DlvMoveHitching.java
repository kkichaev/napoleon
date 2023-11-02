package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DlvMove;
import com.grsoft.dataobjects.DlvMoveItem;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DlvMoveHitching extends RcvNewHitching {
	DlvMoveItemHandler handler = new DlvMoveItemHandler();

	public DlvMoveHitching() {
		super(DlvMove.class, "DeliveryBalanceData");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		handler.onStart();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DlvMove dobj = (DlvMove) rawObject.createDataObject(dataObject);
		dobj.num += DeliveryHitchingEx.sd.format(dobj.docDate);
		dbProxy.insertRecord(dobj);
		postRead(dobj);
	}
	
	@Override
	protected void postRead(DataObject dobj) {
		for(DlvMoveItem di : ((DlvMove)dobj).items) {
			handler.onStep(di);
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		handler.onEnd();
	}
}
