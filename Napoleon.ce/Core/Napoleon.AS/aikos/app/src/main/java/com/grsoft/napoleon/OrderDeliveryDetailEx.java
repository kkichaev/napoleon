package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplEx;

import java.util.List;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail{
    @Override
    protected List<OrderItem> docItems() {
        return ((OrderImplEx)doc).group();
    }
}
