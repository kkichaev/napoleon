package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;

public class DocumentUtils {
	public static void setExported(DbObject<? extends DataObject> dbOject, int params,  boolean value){
		final String FIELD_NAME = "params";
		
		if (value) {
			params |= ParamState.ofExported;
//			params |= ParamState.ofSended;
		} else
			params &= ~ParamState.ofExported;
		
		try {
			DataObject dataObject = dbOject.getData();
			dataObject.getClass().getField(FIELD_NAME).set(dataObject, params);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		dbOject.write();
	}
	
	public static boolean isExported(int params){
		return (params & ParamState.ofExported) == ParamState.ofExported; 
	}
}
