package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name = "MovementAnswer", keyFields = "created")
@ServerInfo(name="MovementAnswer")
public class MovementAnswer extends DataObject {
    public Date created;

    public String number = "";

    public String remark = "";

    public int noedit = 0;

    public List<OrderItem> items = new ArrayList<>();

    public boolean isEmpty() {
        return number.isEmpty() && items.isEmpty();
    }
}
