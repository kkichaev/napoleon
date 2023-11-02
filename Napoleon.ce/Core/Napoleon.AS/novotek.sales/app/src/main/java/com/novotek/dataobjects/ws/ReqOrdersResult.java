package com.novotek.dataobjects.ws;

import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="��������������_v2Response")
public class ReqOrdersResult extends ErrResult {

    @WSDLElement(name="�������������")
    public List<Order> orders = new ArrayList<>();
}
