package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="actdlv", keyFields = "id,number")
@ServerInfo(name="ActDelivery")
public class ActDelivery extends Delivery {

}
