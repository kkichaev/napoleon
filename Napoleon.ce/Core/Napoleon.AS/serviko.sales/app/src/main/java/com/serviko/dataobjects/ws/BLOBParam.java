package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="", memberOrder = "ТелоВызова")
public class BLOBParam {
    @WSDLElement(name="ТелоВызова")
    public BLOBBody body;
}
