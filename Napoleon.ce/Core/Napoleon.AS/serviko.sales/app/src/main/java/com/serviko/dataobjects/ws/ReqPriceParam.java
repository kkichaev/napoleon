package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(memberOrder = "ИдентификаторКонтрагента,ИдентификаторыНоменклатур,ИдентификаторПриложения,ИдентификаторУстройства")
public class ReqPriceParam {
    @WSDLElement(name="ИдентификаторКонтрагента")
    public String orgId = "";

    @WSDLElement(name="ИдентификаторПриложения")
    public String appId = "";

    @WSDLElement(name="ИдентификаторУстройства")
    public String deviceId = "";

    @WSDLElement(name="ИдентификаторыНоменклатур")
    public List<String> ids = new ArrayList<>();
}
