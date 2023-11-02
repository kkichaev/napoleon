package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WSDLElement(name="Значение",memberOrder = "Идентификатор,Дата,ДатаОтгрузки,Товары,Комментарий")
public class OrderSend {

    public OrderSend(Basket src) {
        for(BasketItem oi : src.items) {
            items.add(new OrderSendItem(oi));
        }
        remark = src.remark;
        deliveryDate = src.dlvDate;
        id = src.uid;
    }

    @WSDLElement(name="Идентификатор")
    public String id = "";

    @WSDLElement(name="Дата")
    public Date date = new Date();

    @WSDLElement(name="ДатаОтгрузки")
    public Date deliveryDate = new Date((new Date()).getTime() + 24 * 3600 * 1000);

    @WSDLElement(name="Комментарий")
    public String remark = "";

    @WSDLElement(name="Товары")
    public List<OrderSendItem> items = new ArrayList<>();
}
