package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;

import java.util.List;
import java.util.Map;

public class OrderImplEx extends OrderImpl {
    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
        boolean ret =  super.updateQty(priceImpl, qty, cost, inPack);
        Map<Object, DanaAction> macts = DbReader.fetchDic(DanaAction.class, "id");

        OrderEx o = (OrderEx)data;
        List<DanaAction> undo = o.checkActions(null, macts);
        for(DanaAction da : undo) {
            da.undo(o);
        }

        write();
        return ret;
    }
}
