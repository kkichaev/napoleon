package com.novotek.dataobjects;

public class OrderSendItem {
    public String item_id = "";

    public float qty = 0;

    public OrderSendItem(BasketItem oi) {
        item_id = oi.item.id;
        qty = oi.qty;
    }
}
