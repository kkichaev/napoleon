package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="chatagent", keyFields="id")
@ServerInfo(name="ChatAgent")
public class ChatAgent extends Agent {
	public String userid = "";
}
