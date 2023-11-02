package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orders", keyFields = "created,userid", indexes="userid")
@ServerInfo(name="Order")
public class MOrder extends Order {

}
