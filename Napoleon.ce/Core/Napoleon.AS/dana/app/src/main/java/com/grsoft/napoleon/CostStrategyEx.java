package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceCostItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static String idc = "";
    static Map<String, Integer> cost = new HashMap<>();

    public static void resetCache() {
        idc = "";
        cost.clear();
    }

    static void load(String prcType) {
        if(!idc.equals(prcType)) {
            idc = prcType;
            cost.clear();

            List<PriceCost> res = DbReader.fetch(PriceCost.class, "idc='" + prcType + "'");
            for(PriceCost p : res) {
                for(PriceCostItem pi : p.items) {
                    cost.put(pi.id, pi.cost);
                }
            }
        }
    }

    @Override
    public int getItemCost(Price p, Document<?> doc) {
        if(doc instanceof OrderImpl) {
            load(((Order)doc.getData()).prcType);
            Integer c = cost.get(p.id);
            if(c != null)
                return c;
        }
        return super.getItemCost(p, doc);
    }
}
