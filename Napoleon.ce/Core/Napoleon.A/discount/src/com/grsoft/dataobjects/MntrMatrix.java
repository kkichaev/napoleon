package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MonitoringMatrix", keyFields="name")
@ServerInfo(name="MonitoringMatrix")
public class MntrMatrix extends Matrix {
}
