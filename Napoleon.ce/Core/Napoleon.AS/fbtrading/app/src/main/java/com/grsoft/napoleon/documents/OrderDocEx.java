package com.grsoft.napoleon.documents;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderDocEx extends OrderDoc{
    public OrderDocEx(String docName, String objName, Class<? extends OrderImplBase<? extends Order>> type) {
        super(docName, objName, type);
    }

    public static void init(){
        instance = new OrderDocEx("Заявки", "Order", OrderImplEx.class);
    }

    public int getViewTextColor(Context context, Document<?> doc) {
        if (((OrderImplEx)doc).hasDifferentQtyInDelivery())
            return Color.RED;

        return Color.BLACK;
    }
}
