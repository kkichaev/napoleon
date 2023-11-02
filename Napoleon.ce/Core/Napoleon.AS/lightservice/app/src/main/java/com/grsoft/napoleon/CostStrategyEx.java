package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy{

    public long getCostInt(Price p, Document<?> doc, int sumType) {
        int result = 0;
        if( Features.CAN_CHANGE_COST && doc != null && doc instanceof OrderImplBase<?>) {
            OrderItem oi = (OrderItem)((OrderImplBase<?>)doc).findItem(p.id);
            if( oi != null )
                return oi.cost;
        }
        if( Features.COST_MANAGER != null ) {
            result = Features.COST_MANAGER.getCost(p.id, sumType);
        }

        if(result == 0){
            result = getPriceCost(p, sumType, doc);

            if (doc instanceof OrderImplEx){
                int disc = ((OrderEx)doc.getData()).discount;
                if (disc > 0)
                    result = (int)costWithDiscount(result, disc, Consts.SUM_SCALE);
            }
        }

        return result;
    }
}
