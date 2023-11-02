package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="PriceCost", keyFields = "id,date")
public class PriceCost extends DataObject {
    public String id = "";
    public Date date = new Date();
    public int[] cost = new int[]{};
}
