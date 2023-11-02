package com.grsoft.napoleon;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class OrderDocEx extends OrderDoc {
    public static void init() {
        instance = new OrderDocEx();
    }

    @Override
    public int getViewTextColor(Context context, Document<?> doc) {
        if(doc != null && ((OrderEx)doc.getData()).retdoc > 0) {
            return Color.BLUE;
        }
        return super.getViewTextColor(context, doc);
    }

    OrderDocEx() {
        super("Order", "Order", OrderImplEx.class);
    }
}
