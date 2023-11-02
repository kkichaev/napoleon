package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="btlplan", keyFields="id,cid")
@ServerInfo(name="BtlPlan")
public class BtlPlan extends DataObject {
	public String id = "";
	public String cid = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int face = 0;
	
	public List<BtlPlanItem> items = new ArrayList<BtlPlanItem>();
}
