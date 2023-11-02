package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class WarehouseEx extends Warehouse{
    @Override
    public String formatCellValue(int clmnID, Price price, String value) {
        if (clmnID == COLUMN_COST){
            if (((PriceEx)price).minCost > 0)
                value = value + String.format(" (%s)",
                        Util.IntToScaleStr(((PriceEx)price).minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        }

        return value;
    }
}
