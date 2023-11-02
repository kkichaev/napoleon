package com.grsoft.napoleon;

import android.graphics.Color;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

public class OrderDetailEx extends OrderDetail {

    @Override
    protected void setAdapter() {
        lvItems.setAdapter(new OrderItemsAdapterEx());
    }

    class OrderItemsAdapterEx extends OrderItemsAdapter {
        @Override
        protected int getItemColor(int pos) {
            OrderItemEx oid = (OrderItemEx) getItem(pos);
            return (oid.bonus > 0) ? Color.BLUE : super.getItemColor(pos);
        }
    }
}
