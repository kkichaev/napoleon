package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
    @Override
    public int getCostInt(Price p, Document<?> doc, int sumType) {
        return 1000;
    }
}
