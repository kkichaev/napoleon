package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="DiscountPriority", keyFields = "kind")
@ServerInfo(name="DiscountPriority")
public class DiscountPriority extends DataObject {
    public static final int KIND_TAX = 0;
    public static final int KIND_TU = 1;
    public static final int KIND_PROMO = 2;
    public static final int KIND_TKSG = 3;

    public int kind = KIND_TAX;

    @Scale(value = Consts.SUM_SCALE)
    public int orgCost = 0;

    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;
}
