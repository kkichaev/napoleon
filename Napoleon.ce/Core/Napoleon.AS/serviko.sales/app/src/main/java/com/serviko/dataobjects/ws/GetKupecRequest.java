package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(name="ПолучитьПрайсЛистКупец", memberOrder = "ИдентификаторКонтрагента")
public class GetKupecRequest {
    @WSDLElement(name="ИдентификаторКонтрагента")
    public String id = "";
}
