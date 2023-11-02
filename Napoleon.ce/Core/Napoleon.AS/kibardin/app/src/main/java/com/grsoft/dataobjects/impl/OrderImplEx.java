package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RelatedItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;

import java.util.List;

public class OrderImplEx extends OrderImpl {

    public boolean updateOrder(PriceImpl price, int qty, int inputCost, boolean inPack, List<RelatedItem> related) {

        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        CostStrategy cs = CostStrategy.getInstance(getClass());

        int coef = qty / Consts.QTY_SCALE;

        for(RelatedItem ri : related) {
            p.id = ri.id;
            if(ri.qty == 0 || !pi.read()){
                continue;
            }

            int cost = cs.getItemCost(p, this);
            OrderItem item = (OrderItem) findUpdateItem(p);
            if(item == null) {
                item = new OrderItem();
                item.id = ri.id;
                item.cost = cost;

                data.items.add(item);
            }
            item.qty = ri.qty * coef;
        }

        pi.close();

        return updateQty(price, qty, inputCost, inPack);
    }
}
