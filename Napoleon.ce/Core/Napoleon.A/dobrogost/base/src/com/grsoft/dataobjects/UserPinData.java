package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="UserPinData")
@ServerInfo(name="UserPinData")
public class UserPinData extends DataObject {
	
	public String userid = "";
	public String pinHash = "";
	
	public int authByPin = 0;
	
	/**
	 * Если > 0 - вызываем окно Registration 
	 */
	public int resetPin = 0;
	
	
	public static UserPinData get() {
		UserPinData ret = new UserPinData();
		DbWriter.checkDBTable(ret.getClass());
		
		DbReader r = new DbReader();
		r.select(ret, ret.getTableName(), null);
		r.close();
		
		return ret;
	}
}
