package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


@TableInfo(name="PKO",keyFields="created")
public class PKO extends CreateDocDataObject {
	public String number = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum;

	public String supplyer = "";
	public String dogId = "";
	public int fiscal;
}
