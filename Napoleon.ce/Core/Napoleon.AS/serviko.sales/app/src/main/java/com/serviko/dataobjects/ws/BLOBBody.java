package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="ТелоВызова", memberOrder = "Операция,Параметры")
public class BLOBBody {
    @WSDLElement(name="Операция")
    public String operation = "";

    @WSDLElement(name="Параметры")
    public List<String> params = new ArrayList<>();
}
