package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name = "OrderFulfillment", keyFields = "created")
@ServerInfo(name="OrderFulfillment")
public class OrderFulfillment extends DataObject{
    public Date created = new Date();
    public List<OrderFulfillmentItem> items = new ArrayList<>();
}
