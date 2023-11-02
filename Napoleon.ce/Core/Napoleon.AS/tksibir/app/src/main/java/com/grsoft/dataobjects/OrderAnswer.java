package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name = "OrderAnswer", keyFields = "created")
@ServerInfo(name="OrderAnswer")
public class OrderAnswer extends DataObject{
    public Date created;
    public String number = "";
    public String remark = "";

    public List<OrderItem> items = new ArrayList<>();

    public boolean isEmpty() {
        return number.isEmpty() && items.isEmpty();
    }
}
