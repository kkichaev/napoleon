package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.napoleon.documents.Document;

import java.util.HashMap;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static Map<String, Integer> costData = new HashMap<>();
    static int costIndex = -1;

    public static void resetCache() {
        costData = new HashMap<>();
        costIndex = -1;
    }

    static void load(int ci) {
        if(ci != costIndex) {
            costIndex = ci;
            costData.clear();

            DataTraveler.travel(PriceCost.class, new DataTraveler.Travel<PriceCost>() {
                @Override
                public boolean travel(DataTraveler<PriceCost> item) {
                    if(item.data.cost.length > costIndex && item.data.cost[costIndex] != 0)
                        costData.put(item.data.id, item.data.cost[costIndex]);
                    return true;
                }
            }, "");
        }
    }

    protected int getPriceCost(Price p, int sumType, Document<?> doc) {
        if(sumType < 0)
            return 0;
        load(sumType);
        Integer cost = costData.get(p.id);
        return cost == null ? 0 : cost;
    }
}
