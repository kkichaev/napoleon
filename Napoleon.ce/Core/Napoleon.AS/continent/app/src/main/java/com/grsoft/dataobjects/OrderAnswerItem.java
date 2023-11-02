package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderAnswerItem extends OrderItem {
    @FieldOrder(order = USER_FIELDS)
    @Scale(value = Consts.SUM_SCALE)
    public int sum = 0;
}
