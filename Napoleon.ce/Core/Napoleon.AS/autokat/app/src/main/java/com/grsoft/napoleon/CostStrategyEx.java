package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Selling;
import com.grsoft.dataobjects.StoreData;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
    @Override
    public long getItemCost(Price p, Document<?> doc) {
        if(doc instanceof SellingImpl) {
            int bmark = ((Selling)doc.getData()).bmark;
            for(StoreData sd : ((PriceEx)p).stores) {
                if(sd.bmark == bmark) {
                    return sd.cost;
                }
            }
        }
        return super.getItemCost(p, doc);
    }
}
