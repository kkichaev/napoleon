package com.novotek.dataobjects.ws;

import com.novotek.dataobjects.ws.ReqCodeParam;
import com.novotek.dataobjects.xml.WSDLElement;

@WSDLElement(name="���������������������������_v3", memberOrder = "�������,�����������������������,�����������������������")
public class AcceptCodeParam {
    @WSDLElement(name="�������")
    public String phone = "";

    @WSDLElement(name="�����������������������")
    public String appId = "";

    @WSDLElement(name="�����������������������")
    public String deviceId = "";
}
