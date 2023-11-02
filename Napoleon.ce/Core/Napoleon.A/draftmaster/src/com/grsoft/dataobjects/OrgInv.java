package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@ServerInfo(name="OrgInv")
@TableInfo(name="orginv", keyFields="id,id_i")
public class OrgInv extends DataObject {
	public String id = "";
	public String id_i = "";
	public String name = "";
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;

}
