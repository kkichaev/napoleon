package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class IncomeItem extends DataObject {
    @FieldOrder(order = 0)
    public String code = "";

    @FieldOrder(order = 1)
    public int have = 0;

    @FieldOrder(order = 2)
    public String incomeCode = "";

    public String toText() {
        return code;
    }
}
