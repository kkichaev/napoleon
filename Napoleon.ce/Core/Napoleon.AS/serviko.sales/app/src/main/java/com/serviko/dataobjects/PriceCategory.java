package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

public class PriceCategory {
    @WSDLElement(name="Значение")
    public String name = "";

    @WSDLElement(name="Наименование")
    public String code = "";
}
