package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(name="ВызовСервераResponse")
public class BLOBResult {
    @WSDLElement(name="return")
    public String body = "";
}
