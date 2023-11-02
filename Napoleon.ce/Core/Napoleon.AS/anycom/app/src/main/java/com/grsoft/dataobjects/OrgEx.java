package com.grsoft.dataobjects;

import com.grsoft.util.Util;

import java.util.Date;

public class OrgEx extends Org{

    public static final int DISCOUNT_BY_CREATED = 0;
    public static final int DISCOUNT_BY_DELIVERY = 1;

//    public int discount = 0;
    public String email = "";
    public String ido = "";

    public int dscMode = DISCOUNT_BY_CREATED;

    public int delivery = 0;

    public Date getDiscountDate(Order o) {
        if(o == null || o.created == null)
            return Util.getDayEnd(new Date());
        return dscMode == DISCOUNT_BY_CREATED ? Util.getDayEnd(o.created) : Util.getDayEnd(o.date);
    }
}
