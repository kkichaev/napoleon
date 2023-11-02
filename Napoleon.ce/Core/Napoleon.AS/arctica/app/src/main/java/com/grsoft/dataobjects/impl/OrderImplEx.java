package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
    boolean noFindBonus = false;

    public OrderItemEx getBonus(String id) {
        for(OrderItem oi : data.items) {
            if(oi.id.equals(id)) {
                OrderItemEx oie = (OrderItemEx) oi;
                if(oie.sumType < 0) {
                    return oie;
                }
            }
        }
        return null;
    }

    public void setNoFindBonus(boolean newVal) { noFindBonus = newVal; }

    public void removeBonus(String id) {
        OrderItemEx oie = getBonus(id);
        if(oie != null) {
            data.items.remove(oie);
        }
    }

    @Override
    public DataObject findItem(String itemId) {
        if(!noFindBonus)
            return super.findItem(itemId);

        for(OrderItem oi : data.items) {
            if(oi.id.equals(itemId)) {
                OrderItemEx oie = (OrderItemEx) oi;
                if(oie.sumType >= 0) {
                    return oie;
                }
            }
        }
        return null;
    }

    public void addBonus(String id, int qty, PriceUnit unit) {
        OrderItemEx oie = getBonus(id);
        if(oie == null) {
            oie = new OrderItemEx();
            oie.id = id;
            oie.cost = 0;
            oie.sumType = -1;
            data.items.add(oie);
        }
        oie.flags = 0;
        if(unit != null) {
            oie.flags = OrderItem.IN_PACK;
            oie.unit = unit.id;
            qty = (int)((long)qty * unit.inpack / Consts.QTY_SCALE);
        }
        oie.qty = qty;
    }
}
