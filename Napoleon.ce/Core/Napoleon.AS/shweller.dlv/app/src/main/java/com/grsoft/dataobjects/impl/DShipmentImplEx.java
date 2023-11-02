package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DWaybillDocumentItemEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.WaybillItem;

public class DShipmentImplEx extends DShipmentImpl{
    @Override
    public void initWaybillItem(DeliveryItem di, DWaybillDocumentItem i) {
        super.initWaybillItem(di, i);
        i.outqty = 0;
        ((DWaybillDocumentItemEx)i).unit = ((WaybillItem)di).unit;
    }
}
