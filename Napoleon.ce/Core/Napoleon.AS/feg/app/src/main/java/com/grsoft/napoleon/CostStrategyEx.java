package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgItemDiscount;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FolderTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static String id = "";
    static Map<String, Integer> items = new HashMap<>();
    static List<DiscountSolver> discounts = new ArrayList<>();
    static boolean useTax = false;

    public static void resetCache() {
        id = "";
        discounts.clear();
        items.clear();
    }

    static void load(String orgid) {
        if(!id.equals(orgid)) {
            OrgImpl oi = new OrgImpl();
            OrgEx o = (OrgEx) oi.getData();
            o.id = orgid;
            oi.read();
            oi.close();

            resetCache();

            useTax = o.useTax > 0;
            id = orgid;
            for(OrgItemDiscount oid : o.itemCost) {
                items.put(oid.id, oid.cost);
            }

            FolderTree ft = new FolderTree();
            ft.load();
            for(OrgDiscount od : o.discounts) {
                DiscountSolver ds = DiscountSolver.create(od.id, ft);
                if(ds != null) {
                    discounts.add(ds);
                }
            }
        }
    }

    @Override
    public int getCostInt(Price p, Document<?> doc, int sumType) {
        if(doc != null) {
            load(doc.getId());
            Integer cost = items.get(p.id);
            if(cost  == null) {
                cost = super.getCostInt(p, doc, sumType);
                for(DiscountSolver ds : discounts) {
                    int dc = ds.getCost(cost, p);
                    if(dc != cost) {
                        cost = dc;
                        break;
                    }
                }
            }

            if(useTax) {
                cost = (int)(cost * (1.0 + (float)((PriceEx)p).tax / 100) + 0.5);
            }
            return cost;
        }
        return super.getCostInt(p, doc, sumType);
    }
}
