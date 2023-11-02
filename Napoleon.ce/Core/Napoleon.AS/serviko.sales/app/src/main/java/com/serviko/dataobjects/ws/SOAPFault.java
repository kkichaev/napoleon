package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(name="Fault")
public class SOAPFault {
    @WSDLElement(name="faultcode")
    public String code = "";

    @WSDLElement(name="faultstring")
    public String message = "";
}
