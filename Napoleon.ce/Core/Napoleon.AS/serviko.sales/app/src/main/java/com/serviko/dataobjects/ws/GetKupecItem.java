package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

public class GetKupecItem {
    @WSDLElement(name="Номенклатура")
    public String id = "";

    @WSDLElement(name="НоменклатураПредставление")
    public String name = "";

    @WSDLElement(name="ЦенаНаПолке")
    public float cost = 0;

    @WSDLElement(name="ЦенаПоСкидке")
    public float costDisc = 0;

    @WSDLElement(name="Наценка")
    public float margin = 0;
}
