package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="ПолучитьЗаказы_v2Response")
public class ReqOrdersResult extends ErrResult {

    @WSDLElement(name="МассивЗаказов")
    public List<Order> orders = new ArrayList<>();
}
