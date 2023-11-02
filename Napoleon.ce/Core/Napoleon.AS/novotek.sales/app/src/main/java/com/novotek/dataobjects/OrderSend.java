package com.novotek.dataobjects;

import com.novotek.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderSend {

    public OrderSend(Partner p, Basket src) {
        for(BasketItem oi : src.items) {
            items.add(new OrderSendItem(oi));
        }
        remark = src.remark;
        dateDelivery = src.dlvDate;
        id = src.uid;
        orgid = p.id;
    }

    public String orgid = "";

    public String id = "";

    public Date dateDelivery = new Date((new Date()).getTime() + 24 * 3600 * 1000);

    public String remark = "";

    public String payment = "Наличная";

    public List<OrderSendItem> items = new ArrayList<>();
}
