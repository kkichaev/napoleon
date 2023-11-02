package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(name="ПодтвердитьКодПодтверждения_v3", memberOrder = "Контакт,ИдентификаторПриложения,ИдентификаторУстройства")
public class AcceptCodeParam {
    @WSDLElement(name="Контакт")
    public String phone = "";

    @WSDLElement(name="ИдентификаторПриложения")
    public String appId = "";

    @WSDLElement(name="ИдентификаторУстройства")
    public String deviceId = "";
}
