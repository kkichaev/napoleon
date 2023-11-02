package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

public class Agent {
    @WSDLElement(name = "ФИО")
    public String name = "";

    @WSDLElement(name = "НомерТелефона")
    public String phone = "";

    @WSDLElement(name = "КодКонтракта")
    public String contract = "";
}
