package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
    @Override
    public int getItemValue(Price item) {
        int result = 0;

        if ((((PriceEx) item).items).size() > 0)
			result = (((PriceEx) item).items).get(0).qty;

        return result;
    }
}
