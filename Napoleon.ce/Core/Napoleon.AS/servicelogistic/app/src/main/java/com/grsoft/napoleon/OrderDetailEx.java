package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;

public class OrderDetailEx extends OrderDetail{
    @Override
    protected void deleteDocItem(PriceImpl price) {
        ((OrderImplEx)doc).removeItem(price);
    }
}
