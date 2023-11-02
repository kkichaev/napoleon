package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="orglocation", keyFields="id")
@ServerInfo(name="OrgLocation")
public class OrgLocation extends DataObject {
	public String id = "";
	
	@Scale(value=Consts.GPS_SCALE)
	public int longitude = 0;
	
	@Scale(value=Consts.GPS_SCALE)
	public int latitude = 0;
}
