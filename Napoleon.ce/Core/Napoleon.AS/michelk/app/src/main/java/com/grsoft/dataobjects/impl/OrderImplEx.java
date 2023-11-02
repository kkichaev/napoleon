package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.PQItem;
import com.grsoft.dataobjects.PriceQtyItemEx;

import java.util.HashMap;
import java.util.Map;

public class OrderImplEx extends OrderImpl {

    static Map<String, Integer> priceQty = new HashMap<>();
    static String curSklad = "";

    static void loadSklad(final String ids) {
        if(!curSklad.equals(ids)) {
            priceQty.clear();
            curSklad = ids;

            DataTraveler.travel(PriceQty.class, new DataTraveler.Travel<PriceQty>() {
                @Override
                public boolean travel(DataTraveler<PriceQty> item) {
                    for(PQItem pqi : item.data.sklads) {
                        if(pqi.ids.equals(ids)) {
                            priceQty.put(item.data.id, pqi.qty);
                            break;
                        }
                    }
                    return true;
                }
            }, "");
        }
    }

    @Override
    protected void updatePrice(PriceImpl price, int qty) {
        PriceQty pq = new PriceQty();
        DbReader r = new DbReader();
        final String ids = ((OrderEx)data).whCode;
        String id = price.getData().id;
        if(r.select(pq, pq.getTableName(), "id='" + id + "'")) {
            for(PQItem pqi : pq.sklads) {
                if(pqi.ids.equals(ids)) {
                    pqi.qty += qty;
                    priceQty.put(id, pqi.qty);
                    DbWriter w = new DbWriter();
                    w.insertRecord(pq);
                    w.close();
                    break;
                }
            }
        }
        r.close();
        //super.updatePrice(price, qty);
    }

    public static void clearCache() { curSklad = ""; }

    @Override
    public int getItemValue(Price item) {
        if(((OrderEx)data).whCode.length() == 0) {
            return super.getItemValue(item);
        }
        loadSklad(((OrderEx)data).whCode);
        Integer val = priceQty.get(item.id);
        return val == null ? 0 : val;
    }
}
