package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderAnswer;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
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
    protected boolean checkPriceQty() {
        return ((OrderEx)data).isVan() ? true : super.checkPriceQty();
    }

    @Override
    public int getItemValue(Price item) {
        return ((OrderEx)data).isVan() ? item.vanQty : super.getItemValue(item);
    }

    @Override
    protected void updatePrice(PriceImpl price, int qty) {
        if(((OrderEx)data).isVan()) {
            price.getData().vanQty -= qty;
            price.write();
        } else
            super.updatePrice(price, qty);
    }

    @Override
    public boolean isEditable() {
        return super.isEditable() || ((data.params & SEND_ONLINE) == SEND_ONLINE);
    }
}
