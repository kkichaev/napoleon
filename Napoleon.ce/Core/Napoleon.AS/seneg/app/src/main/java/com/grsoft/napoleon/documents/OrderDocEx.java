package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImpl;

public class OrderDocEx extends OrderDoc {
    public static void init() {
        instance = new OrderDocEx();
    }

    OrderDocEx() {
        super("Заявка", "Order", OrderImpl.class);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
