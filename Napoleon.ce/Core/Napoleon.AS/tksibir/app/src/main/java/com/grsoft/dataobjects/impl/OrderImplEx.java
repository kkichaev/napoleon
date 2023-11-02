package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.napoleon.OrderDeliveryDetailEx;

public class OrderImplEx extends OrderImpl {
    @Override
    public void open(Context context) {
        OrderAnswerInpl oai = new OrderAnswerInpl();
        if(oai.read("created", data.created)) {
            OrderDeliveryDetailEx.open(context, this);
        } else
            super.open(context);
    }

    @Override
    public boolean isEditable() {
        return true; //super.isEditable();
    }
}
