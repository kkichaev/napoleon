package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;
import com.serviko.sales.BuildConfig;

@WSDLElement(name="ПолучитьКодПодтверждения_v2", memberOrder = "Контакт,ИдентификаторПриложения,ИдентификаторУстройства,СпособОтправкиКода,ВерсияПриложения")
public class ReqCodeParam {
    @WSDLElement(name="Контакт")
    public String phone = "";

    @WSDLElement(name="ИдентификаторПриложения")
    public String appId = "";

    @WSDLElement(name="ИдентификаторУстройства")
    public String deviceId = "";

    @WSDLElement(name="СпособОтправкиКода")
    public int byPhone = 0;

    @WSDLElement(name="ВерсияПриложения")
    public int version = BuildConfig.VERSION_CODE;
}

