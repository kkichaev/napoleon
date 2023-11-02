package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SalesItemEx extends SalesItem{
    @FieldVersion(version=1)
    @FieldOrder(order=USER_FIELDS)
    public List<PriceSalesQty> party = new ArrayList<PriceSalesQty>();

    @FieldVersion(version=2)
    @FieldOrder(order=USER_FIELDS+1)
    public String uid = UUID.randomUUID().toString().replace("-", "");


    public int partyQty() {
        int vq = 0;
        for(PriceSalesQty psq : party)
            vq += psq.qty;

        return vq;
    }
}
