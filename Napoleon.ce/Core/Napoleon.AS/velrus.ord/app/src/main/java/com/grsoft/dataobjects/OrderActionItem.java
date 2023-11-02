package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OrderActionItem extends DataObject {
    public String id = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    public List<OrderActionBonus> bonus = new ArrayList<>();
}
