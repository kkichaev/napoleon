package com.grsoft.napoleon;

public class PriceCountEx extends PriceCount {
    @Override
    protected boolean getStartInPack() {
        return price.getData().qtyInPack > 1;
    }
}
