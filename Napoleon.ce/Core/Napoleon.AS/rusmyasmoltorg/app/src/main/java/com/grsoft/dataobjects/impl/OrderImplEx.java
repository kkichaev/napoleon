package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Reserve;
import com.grsoft.dataobjects.ReserveItem;

import java.util.HashMap;
import java.util.Map;

public class OrderImplEx extends OrderImpl {
    Map<String, Integer> reserve = null;

    @Override
    public int getItemValue(Price item) {
        if (reserve == null){
            reserve = new HashMap<>();
            for (Reserve r : DbReader.fetch(Reserve.class, String.format("id='%s'",getId()))){
                for(ReserveItem i : r.items)
                    if (!reserve.containsKey(i.id))
                        reserve.put(i.id, i.qty);
            }

        }

        int rsrv = 0;

        if (reserve.containsKey(item.id))
            rsrv = reserve.get(item.id);

        return super.getItemValue(item) + rsrv;
    }
}
