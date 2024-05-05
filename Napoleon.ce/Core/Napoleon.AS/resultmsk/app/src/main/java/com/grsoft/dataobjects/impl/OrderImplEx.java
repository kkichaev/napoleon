package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Util;

import java.util.Calendar;

public class OrderImplEx extends OrderImpl{
    @Override
    public void postInit() {
        OrgImpl oi = new OrgImpl();
        oi.read("id", data.id);

        Org org = oi.getData();

        data.prcType = org.prcType;
    }

    @Override
    public String getDescription(Context context) {
        if(((OrderEx)data).loadedFromKIS > 0) {
            return "из 1с № " + ((OrderEx)data).orderNumber;
        }
        return super.getDescription(context);
    }

    public void refreshSum() {
        PriceImpl pi = new PriceImpl();
        PriceEx pe = (PriceEx) pi.getData();
        CostStrategy cs = CostStrategy.getInstance(getClass());
        for(OrderItem oi : data.items) {
            pe.id = oi.id;
            if(pi.read()) {
                oi.cost = (int) cs.getItemCost(pe, this);
            }
        }
        pi.close();
    }
}
