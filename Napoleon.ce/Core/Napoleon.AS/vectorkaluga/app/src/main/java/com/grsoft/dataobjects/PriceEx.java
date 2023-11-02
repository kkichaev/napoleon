package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {

    public void putQty(int sqty, int sklad) {
        if(sklad == 0) qty = sqty;
        else {
            while(whQty.size() < sklad) {
                PriceQtyItem pqi = new PriceQtyItem();
                whQty.add(pqi);
            }
            whQty.get(sklad-1).qty = sqty;
        }
    }
}
