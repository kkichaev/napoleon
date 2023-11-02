package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.OrderSend;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(memberOrder = "ИдентификаторыЗаказов,ИдентификаторКонтрагента,ИдентификаторПриложения,ИдентификаторУстройства")
public class SendBasketParam {
    @WSDLElement(name="ИдентификаторКонтрагента")
    public String orgId = "";

    @WSDLElement(name="ИдентификаторПриложения")
    public String appId = "";

    @WSDLElement(name="ИдентификаторУстройства")
    public String deviceId = "";

    @WSDLElement(name="ИдентификаторыЗаказов")
    public List<OrderSend> orders = new ArrayList<>();
}
