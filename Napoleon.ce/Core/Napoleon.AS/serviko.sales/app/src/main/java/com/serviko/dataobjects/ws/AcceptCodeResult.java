package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="ПодтвердитьКодПодтверждения_v3Response")
public class AcceptCodeResult extends ErrResult {
    @WSDLElement(name="ДанныеКонтрагентов")
    public List<Partner> partners = new ArrayList<Partner>();
}
