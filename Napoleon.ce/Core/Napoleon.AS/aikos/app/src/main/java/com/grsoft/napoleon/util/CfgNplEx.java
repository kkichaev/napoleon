package com.grsoft.napoleon.util;

import com.grsoft.napoleon.Warehouse;

public class CfgNplEx extends CfgNpl{
    @Override
    public void resetToDefault() {
        super.resetToDefault();

        priceClmn2Type = Warehouse.COLUMN_QTY_WH;
        priceClmn3Type = Warehouse.COLUMN_COST;
    }
}
