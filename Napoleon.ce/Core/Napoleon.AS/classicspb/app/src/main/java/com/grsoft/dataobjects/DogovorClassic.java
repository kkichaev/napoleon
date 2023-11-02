package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DDogovorsCls", keyFields="id")
@ServerInfo(name="Dogovors")
public class DogovorClassic extends DataObject {
	public String id = "";
	public String firm = "";
	public String idOrg = "";
	public int bonus = 0;
	
	@Override
	public String toString() {
		String ret = firm;
		if(bonus > 0)
			ret += " бонус";
		return ret;
	}
}
