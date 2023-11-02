package com.grsoft.ads.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;


public class TaskDocHelper {
	@SuppressWarnings("unchecked")
	public static <T> T getDoc(String taskid, Class<? extends DataObject> type){
		DbWriter.checkDBTable(type);
		T result = null;
		
		try{
			result = (T) DbObject.getDataType(type).newInstance();
			
			DbReader r = new DbReader();
			
			StringBuilder sb =  new StringBuilder();
			sb.append(" taskid = '").append(taskid).append("'");
			
			String o = "rowid DESC";
			
			if(!r.select((DataObject)result, DataObjectInfo.getInstance().getTableName(type), sb.toString(), o))
				result = null;
			
			r.close();
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
}
