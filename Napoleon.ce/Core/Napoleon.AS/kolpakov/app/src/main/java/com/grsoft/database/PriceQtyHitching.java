package com.grsoft.database;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.HashMap;
import java.util.List;
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
            DbWriter w = new DbWriter();
            List<Sklads> skl = DbReader.fetch(Sklads.class, "", "def desc");
            for(int i=0; i<skl.size(); i++) {
                Sklads s = skl.get(i);
                s.index = i;
                sklads.put(s.id, i);

                w.insertRecord(s);
            }
            w.close();
        }

        PriceEx pe = price.get(pq.id);
        if(pe == null) {
            pe = new PriceEx();
            price.put(pq.id, pe);
        }
        Integer index = sklads.get(pq.storeid);
        if(index != null) {
            pe.putQty(pq.qty, index);
        }
    }
}
