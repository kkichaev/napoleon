package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="SalesBonus", keyFields = "created")
public class SalesBonus extends SalesEx {
    public String docNumber = "";
    public Date docDate = new Date();
}
