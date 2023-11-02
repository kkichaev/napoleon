package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

@WSDLElement(name="ПолучитьКодПодтверждения_v2Response")
public class ReqCodeResult extends ErrResult {
    @WSDLElement(name="КодПодтверждения")
    public int code = 0;
}
