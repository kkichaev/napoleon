package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;

public class SalesBounsPrint extends SalesPrint{
    public SalesBounsPrint(Sales sales) {
        super(sales);
    }

    @Override
    protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
        return new SalesBonusItemPrint((SalesItem) item, index, sales.sumType);
    }
}
