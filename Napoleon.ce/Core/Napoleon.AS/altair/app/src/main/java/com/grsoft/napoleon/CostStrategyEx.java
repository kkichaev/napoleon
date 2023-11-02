package com.grsoft.napoleon;

import com.grsoft.dataobjects.CostEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;

public class CostStrategyEx extends CostStrategy {
    public int getActionCost(Price price, int sumType) {
        int cost = 0;

        if(sumType < price.cost.size()) {
            cost = ((CostEx)price.cost.get(sumType)).actCost;
        }

        return cost;
    }
}
