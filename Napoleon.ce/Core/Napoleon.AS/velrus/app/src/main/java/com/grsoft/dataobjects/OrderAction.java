package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="OrderAction", keyFields="id")
@ServerInfo(name = "OrderAction")
public class OrderAction extends  DataObject {
    public String id;
    public Date start;
    public Date finish;
    public String name = "";
    public String descr = "";
    public Date created;

    public List<OrderActionItem> items = new ArrayList<>();
}
