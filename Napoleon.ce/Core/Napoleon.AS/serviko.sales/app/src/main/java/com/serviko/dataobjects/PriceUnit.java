package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

public class PriceUnit {
    @WSDLElement(name="Идентификатор")
    public String id = "";

    @WSDLElement(name="Наименование")
    public String name = "";

    @WSDLElement(name="Коэффициент")
    public float coef = 1;
}
