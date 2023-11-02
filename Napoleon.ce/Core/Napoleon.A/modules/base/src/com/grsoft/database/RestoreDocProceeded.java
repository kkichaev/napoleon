package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RestoreDocProceeded extends DataObjectRestore {
	protected ProceededDocHandler handler = new ProceededDocHandler();

	public RestoreDocProceeded() {
		super(OrderProceeded.class, "ArchiveOrderProceeded", "created");
	}

	public RestoreDocProceeded(Class<? extends DataObject> dataObject) {
		super(dataObject, "ArchiveOrderProceeded", "created");
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderProceeded dobj = (OrderProceeded) rawObject.createDataObject(dataObject);
		handler.handle(dobj, ParamState.ofProceeded | ParamState.ofExported);
	}
	
	@Override
	public void onEnd() {
		handler.clear();
		super.onEnd();
	}
}
