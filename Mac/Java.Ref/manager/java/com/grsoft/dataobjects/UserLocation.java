package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="userlocation", keyFields="userid,stltime")
@ServerInfo(name="UserLocation")
public class UserLocation extends MGpsPos {

}
