package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Price", keyFields = "id")
@ServerInfo(name="ManagerPrice")
public class ManagerPrice extends Price {

}
