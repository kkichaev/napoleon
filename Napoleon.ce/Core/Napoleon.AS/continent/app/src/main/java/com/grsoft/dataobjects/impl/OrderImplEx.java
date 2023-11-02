package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.napoleon.OrderDeliveryDetailEx;

public class OrderImplEx extends OrderImpl {

    public static final int SEND_ONLINE = 0x1000;
//    public static final int FIXED = 0x1000;

    @Override
    public void open(Context context) {
        OrderAnswerInpl oai = new OrderAnswerInpl();
        if(oai.read("created", data.created)) {
            OrderDeliveryDetailEx.open(context, this);
        } else
            super.open(context);
    }

    public void markSendOnline() {
        data.params |= SEND_ONLINE;
        write();
    }

    @Override
    public boolean isEditable() {
        return super.isEditable() || ((data.params & SEND_ONLINE) == SEND_ONLINE);
    }
}
