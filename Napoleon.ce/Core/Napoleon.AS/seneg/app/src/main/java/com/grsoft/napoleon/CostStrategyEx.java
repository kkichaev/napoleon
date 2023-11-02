package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SenegCostItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceCostImpl;
import com.grsoft.napoleon.documents.Document;

import java.util.HashMap;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static Map<String, SenegCostItem> items = new HashMap<>();
    static String priceID = "";

    public static void resetCache() {
        items = new HashMap<>();
        priceID = "";
    }

    static void load(String pc) {
        if(!priceID.equals(pc)){
            priceID = pc;

            items = new HashMap<>();
            PriceCostImpl pci = new PriceCostImpl();
            if(pci.read("priceID", pc)) {
                for(SenegCostItem sci : pci.getData().items)
                    items.put(sci.id, sci);
            }
        }
    }

    @Override
    public int getItemCost(Price p, Document<?> doc) {
        if( (doc instanceof OrderImpl) && ((Order)doc.getData()).prcType.length() > 0) {
            load(((Order)doc.getData()).prcType);
            SenegCostItem sci = items.get(p.id);
            return sci == null ? 0 : sci.cost;
        }
        return getCostInt(p, doc, 0);
    }
}
