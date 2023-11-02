package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="Pays", keyFields = "id,number")
public class Pays extends DocDataObject {
	@Scale(value=100)
	public int sum;
	
	public String number = "";
}
