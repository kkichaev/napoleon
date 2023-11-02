package com.novotek.dataobjects.ws;

import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="���������������������������_v3Response")
public class AcceptCodeResult extends ErrResult {
    @WSDLElement(name="������������������")
    public List<Partner> partners = new ArrayList<Partner>();
}
