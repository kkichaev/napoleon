package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceCost", keyFields = "id")
public class PriceCost extends DataObject {
    public String id = "";
    public int[] cost = new int[]{};
    public String idItem = "";
}
