package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="ѕолучитьѕрайсЋист упецResponse")
public class GetKupecResponse {
    @WSDLElement(name="–езультат«апросаѕрайсЋист упец_“овары")
    public List<GetKupecItem> items = new ArrayList<>();
}
