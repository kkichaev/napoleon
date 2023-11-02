package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

public class BalanceItem extends DataObject {
    @FieldOrder(order = 0)
    public String number = "";

    @FieldOrder(order = 1)
    public Date date = new Date();

    @FieldOrder(order = 2)
    public Date payDate = new Date();

    @FieldOrder(order = 3)
    @Scale(value= Consts.SUM_SCALE)
    public long sum = 0;

    @FieldOrder(order = 4)
    public String title = "";

    @FieldOrder(order = 5)
    public String type = "";

    @FieldOrder(order = 6)
    @Scale(value= Consts.SUM_SCALE)
    public long sumDoc = 0;

    @FieldOrder(order = 7)
    @FieldVersion(version = 2)
    public String uid = "";

    public boolean isDelivery() { return type.compareTo("Delivery") == 0; }
}
