package com.grsoft.dataobjects;

import com.grsoft.database.ListTypeConvertor;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PurchaseItem extends OrderItem {

    @Scale(value = Consts.WEIGHT_SCALE)
    @FieldOrder(order = USER_FIELDS)
    public int weight = 0;

    public boolean inited() {
        return weight > 0 && cost > 0;
    }
}
