package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="DiscountTree", keyFields = "id")
@ServerInfo(name="DiscountTree")
public class DiscountTree extends DataObject {
    public static final int TYPE_MIN= 0;
    public static final int TYPE_MAX = 1;
    public static final int TYPE_ADD = 2;
    public static final int TYPE_MUL = 3;
    public static final int TYPE_DSPL = 4;

    public String id = "";
    public String parent = "";
    public String name = "";
    public int type = TYPE_MIN;
}
