package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDsc;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

import java.util.HashMap;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {

    static Map<String, Integer> discounts = new HashMap<>();
    static String id = "";

    static void clear() {
        discounts.clear();
        id = "";
    }
    static void load(String orgId) {
        if(!id.equals(orgId)) {
            id = orgId;
            OrgImpl oi = new OrgImpl();
            oi.read("id", orgId);
            for(OrgDsc od : ((OrgEx)oi.getData()).discount) {
                discounts.put(od.id, od.discount);
            }
        }
    }

    @Override
    public long getCostInt(Price p, Document<?> doc, int sumType) {
        long cost = super.getCostInt(p, doc, sumType);
        if(doc != null) {
            load(doc.getId());
            Integer d = discounts.get(p.id);
            if(d != null) {
                cost = costWithDiscount(cost, d, Consts.SUM_SCALE);
            }
        }
        return cost;
    }
}
