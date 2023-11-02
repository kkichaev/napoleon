package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.WhOrderImpl;
import com.grsoft.util.ExtrasConst;

public class WhOrderDetail extends OrderDetailEx {
    static public void open(Context context, OrderImplBase<? extends Order> order) {
        Intent i = new Intent(context, WhOrderDetail.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected OrderImplBase<? extends Order> createDocInstance() {
        return new WhOrderImpl();
    }
}
