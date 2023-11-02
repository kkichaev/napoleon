package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="waybill", keyFields="id,number")
@ServerInfo(name="Waybill")
public class Waybill extends Delivery {

}
