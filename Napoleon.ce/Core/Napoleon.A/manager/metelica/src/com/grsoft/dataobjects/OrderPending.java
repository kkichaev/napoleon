package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orderpending", keyFields="created")
@ServerInfo(name="OrderPending")
public class OrderPending extends Order {

}
