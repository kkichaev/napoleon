package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(memberOrder = "Номенклатура,Количество,Цена")
public class OrderSendItem {
    @WSDLElement(name="Номенклатура")
    public String id = "";

    @WSDLElement(name="Количество")
    public float qty = 0;

    @WSDLElement(name="Цена")
    public float cost = 0;

    public OrderSendItem(BasketItem oi) {
        id = oi.item.id;
        qty = oi.qty;
        cost = oi.cost - oi.discount;
    }

    public OrderSendItem() {}
}
