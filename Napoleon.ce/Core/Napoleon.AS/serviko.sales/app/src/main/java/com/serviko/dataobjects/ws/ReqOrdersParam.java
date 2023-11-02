package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(memberOrder = "ИдентификаторКонтрагента,ИдентификаторПриложения")
public class ReqOrdersParam {
    @WSDLElement(name="ИдентификаторКонтрагента")
    public String orgId = "";

    @WSDLElement(name="ИдентификаторПриложения")
    public String appId = "";
}
