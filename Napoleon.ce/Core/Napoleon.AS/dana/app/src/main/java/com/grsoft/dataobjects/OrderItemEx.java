package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderItemEx extends OrderItem {
    @FieldOrder(order = USER_FIELDS)
    @Scale(value = Consts.SUM_SCALE)
    public int costWOD = 0;

    // для товара в подарок заполняем акцию
    @FieldOrder(order = USER_FIELDS + 1)
    public String action = "";
}
