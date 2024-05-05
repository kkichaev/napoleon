package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderCard;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.CostStrategyEx;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Calendar;
import java.util.Date;

public class OrderImplEx extends OrderImpl{
    @Override
    public void postInit() {
        OrgImpl oi = new OrgImpl();
        oi.read("id", data.id);

        OrderEx o = (OrderEx) data;
        OrgEx org = (OrgEx) oi.getData();

        o.prcType = org.prcType;
        setDeliveryDate(org);
    }

    public boolean setDeliveryDate(OrgEx org) {
        boolean res = false;
        if(org.delivery != 0 && (org.delivery & 0x7f) < 0x7f) {
            Calendar c = Calendar.getInstance();
            Date firstDate = null;

            while (true) {
                c.add(Calendar.DAY_OF_MONTH, 1);
                int dw = c.get(Calendar.DAY_OF_WEEK);
                if (dw != 0) {
                    int f = 1 << (dw - 1);
                    if ((org.delivery & f) != 0) {
                        Date dd = Util.getDayStart(c.getTime());
                        if(data.date.equals(dd)) {
                            break;
                        }
                        if(firstDate == null) {
                            firstDate = dd;
                        }
                        if(data.date.compareTo(dd) < 0) {
                            if(firstDate != null)
                                data.date = firstDate;
                            else
                                data.date = dd;
                            res = true;
                        }
                        break;
                    }
                }
            }
        }

        return res;
    }

    public void refreshSum() {
        PriceImpl pi = new PriceImpl();
        PriceEx pe = (PriceEx) pi.getData();
        CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
        for(OrderItem oi : data.items) {
            pe.id = oi.id;
            if(pi.read()) {
                oi.cost = (int) cs.getItemCost(pe, this);
                oi.sum = (long) oi.cost * oi.qty / Consts.QTY_SCALE;
            }
        }
        pi.close();
    }

    public boolean containsCard(String number) {
        for(OrderCard oc : ((OrderEx)data).cards) {
            if(oc.number.equals(number))
                return true;
        }
        return false;
    }

    public void updateCard(String number) {
        for(OrderCard oc : ((OrderEx)data).cards) {
            if(oc.number.equals(number)) {
                ((OrderEx)data).cards.remove(oc);
                return;
            }
        }
        OrderCard oc = new OrderCard();
        oc.number = number;
        ((OrderEx)data).cards.add(oc);
        return;
    }
}
