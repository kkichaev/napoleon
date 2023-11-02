package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="agenttopsale", keyFields="id")
@ServerInfo(name="AgentTopSale")
public class AgentTopSale extends DataObject {
	public String id = "";
	public String name = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int qty = 0;
}
