package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderAnswerHitching extends Hitching{

    OrderImplEx oi = new OrderImplEx();

    public OrderAnswerHitching() {
        super(OrderAnswer.class);
    }

    @Override
    protected void postRead(DataObject dobj) {
        super.postRead(dobj);
        OrderAnswer oa = (OrderAnswer) dobj;
        Order o = oi.getData();
        o.created = o.created = oa.created;
        if(oi.read()) {
            o.podRemark = oa.remark;
            o.number = oa.number;
            oi.write();
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();
        oi.close();
    }
}
