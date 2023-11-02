package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;

public class SalesPrintEx extends SalesPrint {
    public SalesPrintEx(Sales sales) {
        super(sales);
    }

    @Override
    protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
        return new SalesItemPrintEx((SalesItem) item, index, sales.sumType);
    }
}
