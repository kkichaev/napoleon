package com.grsoft.dataobjects;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@ServerInfo(name="NeedRemove")
public class NeedRemove extends DataObject{
    public Date docCreated;
    public String id;
    public String doctype;
}
