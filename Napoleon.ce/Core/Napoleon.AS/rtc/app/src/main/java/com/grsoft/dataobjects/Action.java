package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@ServerInfo(name="Action")
@TableInfo(name="action", keyFields = "id")
public class Action extends Price{

}
