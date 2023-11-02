package com.grsoft.database;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.HashMap;
import java.util.Map;

public class PriceQtyHitching extends Hitching {

    Map<String, Integer> sklads = null;
    public static Map<String, PriceEx> price = new HashMap<>();

    public PriceQtyHitching() {
        super(PriceQty.class, "PriceQty");
    }

    @Override
    public void onStart() {
        super.onStart();
        price.clear();
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        PriceQty pq = (PriceQty) rawObject.createDataObject(PriceQty.class);

        if(sklads == null) {
            sklads = new HashMap<>();
            DataTraveler.travel(Sklad.class, new DataTraveler.Travel<Sklad>() {
                @Override
                public boolean travel(DataTraveler<Sklad> item) {
                    sklads.put(item.data.id, item.data.index);
                    return true;
                }
            }, "");
        }

        PriceEx pe = price.get(pq.id);
        if(pe == null) {
            pe = new PriceEx();
            price.put(pq.id, pe);
        }
        Integer index = sklads.get(pq.idsklad);
        if(index != null) {
            pe.putQty(pq.qty, index);
        }
    }
}
