package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
    @Override
    protected int getPriceCost(Price p, int sumType, Document<?> doc) {
        int result = super.getPriceCost(p, sumType, doc);

        if ((((PriceEx) p).items).size() > 0)
            result = (((PriceEx) p).items).get(0).cost;

        return result;
    }
}
