package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DlvReturn", keyFields ="id,number")
@ServerInfo(name="DlvReturn")
public class DlvReturn extends DeliveryEx {

}
