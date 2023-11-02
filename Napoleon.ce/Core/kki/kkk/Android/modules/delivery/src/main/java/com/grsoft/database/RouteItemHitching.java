package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.RouteItem;

public class RouteItemHitching extends Hitching {
	public RouteItemHitching() {
		super(RouteItem.class, DataObjectInfo.getInstance().getSrvName(RouteItem.class));
	}
	
//	@Override
//	public void onRead(RawObject rawObject) throws RuntimeException {
//		RouteItem dobj = (RouteItem) rawObject.createDataObject(dataObject);
//		
//		// если точка уже в работе - не подкачивваем заново
//		if(dimpl.readFromId(dobj.itemid) == false) {
//			dbProxy.insertRecord(dobj);		
//		}
//	}
	
}
