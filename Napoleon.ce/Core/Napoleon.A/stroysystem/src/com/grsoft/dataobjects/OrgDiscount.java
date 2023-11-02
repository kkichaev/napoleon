package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrgDiscount",indexes="id")
@ServerInfo(name="OrgDiscount")
public class OrgDiscount extends DataObject {
	public String id = "";
	public String idItem = "";
	public int isFolder = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
}
