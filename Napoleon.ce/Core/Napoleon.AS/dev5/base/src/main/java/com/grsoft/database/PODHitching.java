package com.grsoft.database;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PODHitching extends Hitching {
	public static Class<? extends PODHitching> type = PODHitching.class;
	
	public static PODHitching instance(){
		PODHitching result = null;
		try{
			result = type.newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected ProceededDocHandler handler;
	
	protected PODHitching(Class<? extends DataObject> dataObject, String objectName){
		super(dataObject, objectName);
		handler = createHandler();
	}
	
	protected PODHitching() { 
		this(DbObject.getDataType(OrderProceeded.class), "DocProceeded");
	}
	
	protected ProceededDocHandler createHandler() { return new ProceededDocHandler(); }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderProceeded dobj = (OrderProceeded) rawObject.createDataObject(dataObject);
		handler.handle(dobj, makeParams(dobj));
	}

	protected int makeParams(OrderProceeded op) {
		return ParamState.ofProceeded | ParamState.ofExported;
	}
	
	@Override
	public void onEnd() {
		handler.clear();
		super.onEnd();
	}
}
