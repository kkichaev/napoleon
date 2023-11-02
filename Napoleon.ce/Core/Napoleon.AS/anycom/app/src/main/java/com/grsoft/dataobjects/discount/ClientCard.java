package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

import java.util.Date;

@TableInfo(name="ClientCard", keyFields = "number")
@ServerInfo(name="ClientCard")
public class ClientCard extends DataObject {
    public String number = "";
    public String name = "";
    public String idDsc = "";

    public Date start = new Date();
    public Date finish = new Date();
};
