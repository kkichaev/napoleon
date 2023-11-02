package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

public class OrderItem {
    @WSDLElement(name="Номенклатура")
    public String id = "";

    @WSDLElement(name="НоменклатураПредставление")
    public String name = "";

    @WSDLElement(name="ЗаказаноКоличество")
    public float qty = 0;

    @WSDLElement(name="ЗаказаноСумма")
    public float sum = 0;

    @WSDLElement(name="ФактКоличество")
    public float qtyFact = 0;

    @WSDLElement(name="ФактСумма")
    public float sumFact = 0;
}
