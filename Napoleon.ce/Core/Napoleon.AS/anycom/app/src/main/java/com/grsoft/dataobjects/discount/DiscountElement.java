package com.grsoft.dataobjects.discount;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DiscountElement extends DataObject {
    static final  int TYPE_ORG_COST = 1;
    static final  int TYPE_ORG_DISCOUNT = 0;

    public String id = "";

    @Scale(value = 1000)
    public int discount = 0;

    public int priority = 0;
    public int kind = 0;

    public int orgCost = TYPE_ORG_DISCOUNT;

    public String parent = "";
    public String name = "";
}
