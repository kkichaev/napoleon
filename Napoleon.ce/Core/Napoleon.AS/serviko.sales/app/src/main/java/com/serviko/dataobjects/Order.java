package com.serviko.dataobjects;

import android.content.Context;

import com.serviko.dataobjects.xml.WSDLElement;
import com.serviko.sales.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order implements Comparable<Order> {
    public static final int ORDER_STATE_ACTIVE = 1;
    public static final int ORDER_STATE_DONE = 2;
    public static final int ORDER_STATE_DEBT = 3;

    public static final String HANDLING = "В обработке";
    public static final String PACKAGING = "Передано на сборку";
    public static final String BY_WAY = "В пути";
    public static final String DELIVERED = "Доставлен";
    public static final String DELIVERED_O = "Доставлено";
    public static final String CANCELED = "Отменен";

    public String uid = UUID.randomUUID().toString().replace("-", "");

    @WSDLElement(name="Представление")
    public String text = "";

    @WSDLElement(name="Номер")
    public String number = "";

    @WSDLElement(name="Дата")
    public Date orderDate = new Date();

    @WSDLElement(name="ДатаОтгрузки")
    public Date deliveryDate = new Date();

    @WSDLElement(name="СтатусЗаказа")
    public String status = "";

    @WSDLElement(name="СостояниеЗаказа")
    public int state = 1;

    @WSDLElement(name="Задолженность")
    public float debtSum = 0;

    @WSDLElement(name="Комментарий")
    public String remark = "";

    @WSDLElement(name="Товары")
    public List<OrderItem> items = new ArrayList<>();

    public boolean inState(int state) {
        if(state == ORDER_STATE_ACTIVE)
            return this.state ==  ORDER_STATE_ACTIVE;
        if(state == ORDER_STATE_DONE)
            return this.state ==  ORDER_STATE_DONE;
        if(state == ORDER_STATE_DEBT)
            return isUnpayed();

        return false;
    }

    public boolean isUnpayed() {
        if(status.equals(PACKAGING) || status.equals(BY_WAY) || status.equals(DELIVERED) || status.equals(DELIVERED_O))
            return debtSum > 0;
        return true;
    }

    public String getStateText(Context context) {
        return status;
//        return state == ORDER_STATE_DONE ? R.string.order_done :
//                R.string.order_active;

    }

    public float sum() {
        float ret = 0;
        for (OrderItem oi : items)
            ret += oi.sum;
        return ret;
    }

    public float sumFact() {
        float ret = 0;
        for (OrderItem oi : items)
            ret += oi.sumFact;
        return ret;
    }

    @Override
    public int compareTo(Order order) {
        return order.orderDate.compareTo(orderDate);
    }
}
