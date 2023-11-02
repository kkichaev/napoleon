package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;

public class DShipmentImplEx extends DShipmentImpl{
    @Override
    public void initWaybillItem(DeliveryItem di, DWaybillDocumentItem i) {
        super.initWaybillItem(di, i);
        i.outqty = 0;
    }
}
