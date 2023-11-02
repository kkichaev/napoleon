package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PhoneCall", keyFields="created")
@ServerInfo(name="PhoneCall")
public class PhoneCall extends CreateDocDataObject {
	
	public int actions = 0;

}
