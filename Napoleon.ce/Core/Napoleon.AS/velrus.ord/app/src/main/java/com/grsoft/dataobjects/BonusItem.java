package com.grsoft.dataobjects;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.types.FieldOrder;

public class BonusItem extends OrderItem {
    @FieldOrder(order = USER_FIELDS+1)
    public String bonusID = "";
}
