package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="Return", keyFields="created", indexes = "id,ordcrt")
public class ReturnEx extends Return{
    public Date ordcrt;
}
