package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="mfr", keyFields = "id")
@ServerInfo(name="Manufactor")
public class Manufactor extends DataObject {
    public String id = "";
    public String name = "";
}
