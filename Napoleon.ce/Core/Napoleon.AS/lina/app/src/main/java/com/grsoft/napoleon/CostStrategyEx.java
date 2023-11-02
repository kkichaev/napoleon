package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgCostItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

import java.util.HashMap;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static String id = "";
    static Map<String, Integer> cost = new HashMap<>();

    public static void clear() {
        cost = new HashMap<>();
        id = "";
    }

    static void load(String orgid) {
        if(id.equals(orgid) == false) {
            OrgImpl oi = new OrgImpl();
            OrgEx oe = (OrgEx) oi.getData();
            oe.id = orgid;
            oi.read();
            oi.close();

            id = orgid;
            cost = new HashMap<>();
            for(OrgCost oc : DbReader.fetch(OrgCost.class, "id='" + oe.ido + "'")) {
                for(OrgCostItem oci:oc.items) {
                    cost.put(oci.id, oci.cost);
                }
            }
        }
    }

    @Override
    public long getItemCost(Price p, Document<?> doc) {
        if(doc != null) {
            load(doc.getId());
            Integer c = cost.get(p.id);
            if(c != null)
                return c;
        }
        return super.getItemCost(p, doc);
    }
}
