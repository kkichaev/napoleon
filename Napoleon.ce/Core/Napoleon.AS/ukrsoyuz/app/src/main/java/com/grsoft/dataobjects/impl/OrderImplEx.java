package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl{
    UnitItem unit = null;

    public boolean updateQty(PriceImpl priceImpl, int qty, int cost, UnitItem unit) {
        this.unit = unit;
        if(unit != null && unit.inpack != Consts.QTY_SCALE) {
            qty = (int)((long)qty * unit.inpack / Consts.QTY_SCALE);
        }
        return super.updateQty(priceImpl, qty, cost, false);
    }

    @Override
    protected void beforeItemWrite(OrderItem item, Price p) {
        if(unit != null) {
            ((OrderItemEx)item).unit = unit.id;
            if(unit.inpack != Consts.QTY_SCALE)
                item.flags |= OrderItem.IN_PACK;
            unit = null;
        }
        super.beforeItemWrite(item, p);
    }
}
