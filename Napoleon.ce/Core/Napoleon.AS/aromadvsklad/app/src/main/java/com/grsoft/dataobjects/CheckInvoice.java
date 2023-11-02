package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="CheckInvoice", keyFields = "number")
@ServerInfo(name="CheckInvoice")
public class CheckInvoice extends DataObject {
    public String number = "";
    public String name = "";
    public Date date = new Date();

    public List<CheckItem> items = new ArrayList<>();

}
