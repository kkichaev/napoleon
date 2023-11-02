package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPriceCost;
import com.grsoft.dataobjects.OrgPriceType;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
    static OrgEx org = null;

    public static void resetCache() {
        org = null;
    }

    static void load(String id) {
        if(org == null || !org.id.equals(id)) {
            OrgImpl oi = new OrgImpl();
            org = (OrgEx) oi.getData();
            org.id = id;
            oi.read();
            oi.close();
        }
    }

    @Override
    protected int getPriceCost(Price p, int sumType, Document<?> doc) {
        if(doc != null && doc.getId().length() > 0) {
            PriceEx pe = (PriceEx) p;
            load(doc.getId());
            for(OrgPriceType opt : org.groupType) {
                if(opt.group.equals(pe.group)) {
                    sumType = opt.costype;
                    break;
                }
            }
            int cost = super.getPriceCost(p, sumType, doc);
            for(OrgPriceCost opc : org.itemCost) {
                if(opc.id.equals(p.id)) {
                    double cd = (double)cost / Consts.SUM_SCALE;
                    double dsc = (double)opc.discount / (Consts.SUM_SCALE * 100.0);
                    double discCost = cd * dsc;
                    double limit = (double)opc.limit / Consts.SUM_SCALE;
                    if(discCost > limit ) {
                        discCost = limit;
                    }
                    cost = (int)((cd - discCost) * Consts.SUM_SCALE + 0.5);
                }
            }
            return cost;
        }
        return super.getPriceCost(p, sumType, doc);
    }
}
