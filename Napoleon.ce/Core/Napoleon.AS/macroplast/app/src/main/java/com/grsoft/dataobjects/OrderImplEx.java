package com.grsoft.dataobjects;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class OrderImplEx extends OrderImpl {
    @Override
    public void updateItemsCost(int sumType) {
        List<KeyValue> values = new ArrayList<>();
        ConfigImpl ci = new ConfigImpl();
        if(ci.read("key", "¬ид÷ены")) {
            DialogHelper.makeListWithKey(ci.getData().value, values, "");
            if(values.size() > sumType) {
                String newPc = values.get(sumType).key.toString();
                PriceImpl pi = new PriceImpl();
                PriceEx pe = (PriceEx) pi.getData();

                for(OrderItem oi : data.items) {
                    OrderItemEx oid = (OrderItemEx) oi;
                    if(oi.id.equals(data.prcType)) {
                        pe.id = oi.id;
                        pi.read();

                        long cost = CostStrategy.defaultInstance.getCostInt(pe, this, sumType);
                        oid.prcType = newPc;
                        oid.cost = (int)cost;
                    }
                }
                pi.close();
                data.prcType = newPc;
                write();
            }
        } else {
            super.updateItemsCost(sumType);
        }
    }
}
