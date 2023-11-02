package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="reserve",keyFields = "id")
@ServerInfo(name="Reserve")
public class Reserve extends DataObject{
    public String id = "";
    public List<ReserveItem> items = new ArrayList<>();
}
