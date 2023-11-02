package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="PriceCost", keyFields = "id")
public class PriceCost extends DataObject {
    public String id = "";
    public int[] cost = new int[]{};
}
