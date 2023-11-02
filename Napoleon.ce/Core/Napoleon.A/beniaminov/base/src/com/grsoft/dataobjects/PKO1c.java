package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="PKO1c", keyFields = "id,number")
public class PKO1c extends DocDataObject {
	@Scale(value=100)
	public long sum;
	
	public String number = "";
	public String ido = "";
	public Date created;
}
