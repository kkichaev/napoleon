package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="relivery", keyFields = "id,number")
@ServerInfo(name="Relivery")
public class Relivery extends Delivery {

}
