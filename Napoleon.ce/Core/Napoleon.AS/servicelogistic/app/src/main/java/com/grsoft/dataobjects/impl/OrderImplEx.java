package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgAsmMatrix;
import com.grsoft.dataobjects.impl.OrderImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderImplEx extends OrderImpl {
    public boolean isComplete(List<MatrixItem> src) {
        if(((OrderEx)data).retdoc != 0)
            return true;

        if(((OrderEx)data).writeoff != 0)
            return true;

        Set<String> items = new HashSet<>();
        for(MatrixItem mi : src) {
            items.add(mi.id);
        }

        for(OrderItem oi : data.items) {
            items.remove(oi.id);
        }

        for(OrderItem oi : ((OrderEx)data).used) {
            items.remove(oi.id);
        }

        return items.size() == 0;
    }

    public boolean itemChecked(String id) {
        for(OrderItem oi : ((OrderEx)data).used) {
            if(oi.id.equals(id))
                return true;
        }

        return false;
    }

    @Override
    public boolean updateQty(PriceImpl item, int qty, long cost, boolean inPack) {
        if(OrgAsmMatrix.needCheckAssortment(data.id)) {
            String id = item.getData().id;
            if(qty == 0 && !itemChecked(id)) {
                OrderItem oi = new OrderItem();
                oi.id = id;
                ((OrderEx)data).used.add(oi);
                write();
                return true;
            }
        }
        return super.updateQty(item, qty, cost, inPack);
    }

    public void removeItem(PriceImpl price) {
        super.updateQty(price, 0, 0, false);
    }
}
