package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSegment;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {

    static OrgEx org = null;

    public static void resetCache() {
        org = null;
    }

    static void load(String id) {
        if(org == null || org.equals(id) == false) {
            OrgImpl oi = new OrgImpl();
            org = (OrgEx) oi.getData();
            org.id = id;
            oi.read();
            oi.close();
        }
    }

    @Override
    protected int getPriceCost(Price p, int sumType, Document<?> doc) {
        if(doc instanceof OrderImpl) {
            load(doc.getId());
            OrderItem oi = (OrderItem) ((OrderImpl)doc).findItem(p.id);
            PriceEx pe = (PriceEx) p;
            if(pe.segment.length() > 0) {
                int cost = pe.baseCost;
                int nac = 0;
                int tarif = 0;
                for (OrgSegment os : org.segments) {
                    if(os.id.length() == 0)
                        continue;

                    if (pe.segment.contains(os.id)) {
                        nac += os.nac;
                        if(tarif < os.tarif)
                            tarif = os.tarif;
                    }
                }
                if(nac > 0 || tarif > 0) {
                    int c1 = (int) costWithDiscount(cost, -nac, Consts.SUM_SCALE);
                    if (oi != null && pe.weight != 0 && tarif != 0) {
                        c1 += (int) (tarif * ((double) pe.weight / Consts.WEIGHT_SCALE) + 0.05);
                    }
                    if (c1 > cost)
                        cost = c1;
                }
                return cost;
            }
        }
        return super.getPriceCost(p, sumType, doc);
    }
}
